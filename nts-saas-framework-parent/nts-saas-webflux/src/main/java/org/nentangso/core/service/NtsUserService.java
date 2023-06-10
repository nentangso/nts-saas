package org.nentangso.core.service;

import org.nentangso.core.config.NtsConstants;
import org.nentangso.core.domain.NtsAuthority;
import org.nentangso.core.domain.NtsUserEntity;
import org.nentangso.core.repository.NtsAuthorityRepository;
import org.nentangso.core.repository.NtsUserRepository;
import org.nentangso.core.security.NtsSecurityUtils;
import org.nentangso.core.service.dto.NtsAdminUserDTO;
import org.nentangso.core.service.dto.NtsUserDTO;
import org.nentangso.core.service.mapper.NtsUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@ConditionalOnMissingBean(name = "userService")
public class NtsUserService {
    private final Logger log = LoggerFactory.getLogger(NtsUserService.class);

    protected final NtsUserRepository userRepository;

    protected final NtsAuthorityRepository authorityRepository;

    protected final NtsUserMapper userMapper;

    public NtsUserService(NtsUserRepository userRepository, NtsAuthorityRepository authorityRepository, NtsUserMapper userMapper) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.userMapper = userMapper;
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     * @return a completed {@link Mono}.
     */
    @Transactional
    public Mono<Void> updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        return NtsSecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setLangKey(langKey);
                user.setImageUrl(imageUrl);
                return saveUser(user);
            })
            .doOnNext(user -> log.debug("Changed Information for User: {}", user))
            .then();
    }

    @Transactional
    public Mono<NtsUserEntity> saveUser(NtsUserEntity user) {
        return saveUser(user, false);
    }

    @Transactional
    public Mono<NtsUserEntity> saveUser(NtsUserEntity user, boolean forceCreate) {
        return NtsSecurityUtils
            .getCurrentUserLogin()
            .switchIfEmpty(Mono.just(NtsConstants.SYSTEM))
            .flatMap(login -> {
                if (user.getCreatedBy() == null) {
                    user.setCreatedBy(login);
                }
                user.setUpdatedBy(login);
                // Saving the relationship can be done in an entity callback
                // once https://github.com/spring-projects/spring-data-r2dbc/issues/215 is done
                Mono<NtsUserEntity> persistedUser;
                if (forceCreate) {
                    persistedUser = userRepository.create(user);
                } else {
                    persistedUser = userRepository.save(user);
                }
                return persistedUser.flatMap(savedUser ->
                    Flux
                        .fromIterable(user.getAuthorities())
                        .flatMap(authority -> userRepository.saveUserAuthority(savedUser.getId(), authority.getName()))
                        .then(Mono.just(savedUser))
                );
            });
    }

    @Transactional(readOnly = true)
    public Flux<NtsAdminUserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllWithAuthorities(pageable)
            .map(userMapper::userToAdminUserDTO);
    }

    @Transactional(readOnly = true)
    public Flux<NtsUserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable)
            .map(userMapper::userToUserDTO);
    }

    @Transactional(readOnly = true)
    public Mono<Long> countManagedUsers() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public Mono<NtsUserEntity> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    /**
     * Gets a list of all the authorities.
     *
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public Flux<String> getAuthorities() {
        return authorityRepository.findAll().map(NtsAuthority::getName);
    }

    protected Mono<NtsUserEntity> syncUserWithIdP(Map<String, Object> details, NtsUserEntity user) {
        // save authorities in to sync user roles/groups between IdP and JHipster's local database
        Collection<String> userAuthorities = user.getAuthorities().stream().map(NtsAuthority::getName).collect(Collectors.toList());

        return getAuthorities()
            .collectList()
            .flatMapMany(dbAuthorities -> {
                List<NtsAuthority> authoritiesToSave = userAuthorities
                    .stream()
                    .filter(authority -> !dbAuthorities.contains(authority))
                    .map(authority -> {
                        NtsAuthority authorityToSave = new NtsAuthority();
                        authorityToSave.setName(authority);
                        return authorityToSave;
                    })
                    .collect(Collectors.toList());
                return Flux.fromIterable(authoritiesToSave);
            })
            .doOnNext(authority -> log.debug("Saving authority '{}' in local database", authority))
            .flatMap(authorityRepository::save)
            .then(userRepository.findOneByLogin(user.getLogin()))
            .switchIfEmpty(saveUser(user, true))
            .flatMap(existingUser -> {
                // if IdP sends last updated information, use it to determine if an update should happen
                if (details.get("updated_at") != null) {
                    Instant dbModifiedDate = existingUser.getUpdatedAt();
                    Instant idpModifiedDate;
                    if (details.get("updated_at") instanceof Instant) {
                        idpModifiedDate = (Instant) details.get("updated_at");
                    } else {
                        idpModifiedDate = Instant.ofEpochSecond((Integer) details.get("updated_at"));
                    }
                    if (idpModifiedDate.isAfter(dbModifiedDate)) {
                        log.debug("Updating user '{}' in local database", user.getLogin());
                        return updateUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getLangKey(), user.getImageUrl());
                    }
                    // no last updated info, blindly update
                } else {
                    log.debug("Updating user '{}' in local database", user.getLogin());
                    return updateUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getLangKey(), user.getImageUrl());
                }
                return Mono.empty();
            })
            .thenReturn(user);
    }

    /**
     * Returns the user from an OAuth 2.0 login or resource server with JWT.
     * Synchronizes the user in the local repository.
     *
     * @param authToken the authentication token.
     * @return the user from the authentication.
     */
    @Transactional
    public Mono<NtsAdminUserDTO> getUserFromAuthentication(AbstractAuthenticationToken authToken) {
        Map<String, Object> attributes;
        if (authToken instanceof OAuth2AuthenticationToken) {
            attributes = ((OAuth2AuthenticationToken) authToken).getPrincipal().getAttributes();
        } else if (authToken instanceof JwtAuthenticationToken) {
            attributes = ((JwtAuthenticationToken) authToken).getTokenAttributes();
        } else {
            throw new IllegalArgumentException("AuthenticationToken is not OAuth2 or JWT!");
        }
        NtsUserEntity user = getUser(attributes);
        user.setAuthorities(
            authToken
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> {
                    NtsAuthority auth = new NtsAuthority();
                    auth.setName(authority);
                    return auth;
                })
                .collect(Collectors.toSet())
        );

        return syncUserWithIdP(attributes, user)
            .flatMap(u -> Mono.just(userMapper.userToAdminUserDTO(user)));
    }

    protected NtsUserEntity getUser(Map<String, Object> details) {
        NtsUserEntity user = new NtsUserEntity();
        Boolean activated = Boolean.TRUE;
        String sub = String.valueOf(details.get("sub"));
        String username = null;
        if (details.get("preferred_username") != null) {
            username = ((String) details.get("preferred_username")).toLowerCase();
        }
        // handle resource server JWT, where sub claim is email and uid is ID
        if (details.get("uid") != null) {
            user.setId((String) details.get("uid"));
            user.setLogin(sub);
        } else {
            user.setId(sub);
        }
        if (username != null) {
            user.setLogin(username);
        } else if (user.getLogin() == null) {
            user.setLogin(user.getId());
        }
        if (details.get("given_name") != null) {
            user.setFirstName((String) details.get("given_name"));
        } else if (details.get("name") != null) {
            user.setFirstName((String) details.get("name"));
        }
        if (details.get("family_name") != null) {
            user.setLastName((String) details.get("family_name"));
        }
        if (details.get("email_verified") != null) {
            activated = (Boolean) details.get("email_verified");
        }
        if (details.get("email") != null) {
            user.setEmail(((String) details.get("email")).toLowerCase());
        } else if (sub.contains("|") && (username != null && username.contains("@"))) {
            // special handling for Auth0
            user.setEmail(username);
        } else {
            user.setEmail(sub);
        }
        if (details.get("langKey") != null) {
            user.setLangKey((String) details.get("langKey"));
        } else if (details.get("locale") != null) {
            // trim off country code if it exists
            String locale = (String) details.get("locale");
            if (locale.contains("_")) {
                locale = locale.substring(0, locale.indexOf('_'));
            } else if (locale.contains("-")) {
                locale = locale.substring(0, locale.indexOf('-'));
            }
            user.setLangKey(locale.toLowerCase());
        } else {
            // set langKey to default if not specified by IdP
            user.setLangKey(NtsConstants.DEFAULT_LANGUAGE);
        }
        if (details.get("picture") != null) {
            user.setImageUrl((String) details.get("picture"));
        }
        user.setActivated(activated);
        return user;
    }
}
