package org.nentangso.core.service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.nentangso.core.domain.NtsAuthority;
import org.nentangso.core.domain.NtsUserEntity;
import org.nentangso.core.service.dto.NtsAdminUserDTO;
import org.nentangso.core.service.dto.NtsUserDTO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mapper for the entity {@link NtsUserEntity} and its DTO called {@link NtsUserDTO}.
 *
 * Normal mappers are generated using MapStruct, this one is hand-coded as MapStruct
 * support is still in beta, and requires a manual step with an IDE.
 */
@Service
@ConditionalOnMissingBean(name = "userMapper")
public class NtsUserMapper {

    public List<NtsUserDTO> usersToUserDTOs(List<NtsUserEntity> users) {
        return users.stream().filter(Objects::nonNull).map(this::userToUserDTO).collect(Collectors.toList());
    }

    public NtsUserDTO userToUserDTO(NtsUserEntity user) {
        NtsUserDTO userDTO = new NtsUserDTO();
        userDTO.setId(user.getId());
        userDTO.setLogin(user.getLogin());
        return userDTO;
    }

    public List<NtsAdminUserDTO> usersToAdminUserDTOs(List<NtsUserEntity> users) {
        return users.stream().filter(Objects::nonNull).map(this::userToAdminUserDTO).collect(Collectors.toList());
    }

    public NtsAdminUserDTO userToAdminUserDTO(NtsUserEntity user) {
        NtsAdminUserDTO adminUserDTO = new NtsAdminUserDTO();
        adminUserDTO.setId(user.getId());
        adminUserDTO.setLogin(user.getLogin());
        adminUserDTO.setFirstName(user.getFirstName());
        adminUserDTO.setLastName(user.getLastName());
        adminUserDTO.setEmail(user.getEmail());
        adminUserDTO.setActivated(user.isActivated());
        adminUserDTO.setImageUrl(user.getImageUrl());
        adminUserDTO.setLangKey(user.getLangKey());
        adminUserDTO.setCreatedBy(user.getCreatedBy());
        adminUserDTO.setCreatedAt(user.getCreatedAt());
        adminUserDTO.setUpdatedBy(user.getUpdatedBy());
        adminUserDTO.setUpdatedAt(user.getUpdatedAt());
        var authorities = user.getAuthorities().stream().map(NtsAuthority::getName).collect(Collectors.toSet());
        adminUserDTO.setAuthorities(authorities);
        return adminUserDTO;
    }

    public List<NtsUserEntity> userDTOsToUsers(List<NtsAdminUserDTO> userDTOs) {
        return userDTOs.stream().filter(Objects::nonNull).map(this::userDTOToUser).collect(Collectors.toList());
    }

    public NtsUserEntity userDTOToUser(NtsAdminUserDTO userDTO) {
        if (userDTO == null) {
            return null;
        } else {
            NtsUserEntity user = new NtsUserEntity();
            user.setId(userDTO.getId());
            user.setLogin(userDTO.getLogin());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setImageUrl(userDTO.getImageUrl());
            user.setActivated(userDTO.isActivated());
            user.setLangKey(userDTO.getLangKey());
            Set<NtsAuthority> authorities = this.authoritiesFromStrings(userDTO.getAuthorities());
            user.setAuthorities(authorities);
            return user;
        }
    }

    private Set<NtsAuthority> authoritiesFromStrings(Set<String> authoritiesAsString) {
        Set<NtsAuthority> authorities = new HashSet<>();

        if (authoritiesAsString != null) {
            authorities =
                authoritiesAsString
                    .stream()
                    .map(string -> {
                        NtsAuthority auth = new NtsAuthority();
                        auth.setName(string);
                        return auth;
                    })
                    .collect(Collectors.toSet());
        }

        return authorities;
    }

    public NtsUserEntity userFromId(String id) {
        if (id == null) {
            return null;
        }
        NtsUserEntity user = new NtsUserEntity();
        user.setId(id);
        return user;
    }

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    public NtsUserDTO toDtoId(NtsUserEntity user) {
        if (user == null) {
            return null;
        }
        NtsUserDTO userDto = new NtsUserDTO();
        userDto.setId(user.getId());
        return userDto;
    }

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    public Set<NtsUserDTO> toDtoIdSet(Set<NtsUserEntity> users) {
        if (users == null) {
            return Collections.emptySet();
        }

        Set<NtsUserDTO> userSet = new HashSet<>();
        for (NtsUserEntity userEntity : users) {
            userSet.add(this.toDtoId(userEntity));
        }

        return userSet;
    }

    @Named("login")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    public NtsUserDTO toDtoLogin(NtsUserEntity user) {
        if (user == null) {
            return null;
        }
        NtsUserDTO userDto = new NtsUserDTO();
        userDto.setId(user.getId());
        userDto.setLogin(user.getLogin());
        return userDto;
    }

    @Named("loginSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    public Set<NtsUserDTO> toDtoLoginSet(Set<NtsUserEntity> users) {
        if (users == null) {
            return Collections.emptySet();
        }

        Set<NtsUserDTO> userSet = new HashSet<>();
        for (NtsUserEntity userEntity : users) {
            userSet.add(this.toDtoLogin(userEntity));
        }

        return userSet;
    }
}
