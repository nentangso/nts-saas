package org.nentangso.core.service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.nentangso.core.domain.Authority;
import org.nentangso.core.domain.UserEntity;
import org.nentangso.core.service.dto.AdminUserDTO;
import org.nentangso.core.service.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mapper for the entity {@link UserEntity} and its DTO called {@link UserDTO}.
 *
 * Normal mappers are generated using MapStruct, this one is hand-coded as MapStruct
 * support is still in beta, and requires a manual step with an IDE.
 */
@Service
public class UserMapper {

    public List<UserDTO> usersToUserDTOs(List<UserEntity> users) {
        return users.stream().filter(Objects::nonNull).map(this::userToUserDTO).collect(Collectors.toList());
    }

    public UserDTO userToUserDTO(UserEntity user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setLogin(user.getLogin());
        return userDTO;
    }

    public List<AdminUserDTO> usersToAdminUserDTOs(List<UserEntity> users) {
        return users.stream().filter(Objects::nonNull).map(this::userToAdminUserDTO).collect(Collectors.toList());
    }

    public AdminUserDTO userToAdminUserDTO(UserEntity user) {
        AdminUserDTO adminUserDTO = new AdminUserDTO();
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
        var authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
        adminUserDTO.setAuthorities(authorities);
        return adminUserDTO;
    }

    public List<UserEntity> userDTOsToUsers(List<AdminUserDTO> userDTOs) {
        return userDTOs.stream().filter(Objects::nonNull).map(this::userDTOToUser).collect(Collectors.toList());
    }

    public UserEntity userDTOToUser(AdminUserDTO userDTO) {
        if (userDTO == null) {
            return null;
        } else {
            UserEntity user = new UserEntity();
            user.setId(userDTO.getId());
            user.setLogin(userDTO.getLogin());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setImageUrl(userDTO.getImageUrl());
            user.setActivated(userDTO.isActivated());
            user.setLangKey(userDTO.getLangKey());
            Set<Authority> authorities = this.authoritiesFromStrings(userDTO.getAuthorities());
            user.setAuthorities(authorities);
            return user;
        }
    }

    private Set<Authority> authoritiesFromStrings(Set<String> authoritiesAsString) {
        Set<Authority> authorities = new HashSet<>();

        if (authoritiesAsString != null) {
            authorities =
                authoritiesAsString
                    .stream()
                    .map(string -> {
                        Authority auth = new Authority();
                        auth.setName(string);
                        return auth;
                    })
                    .collect(Collectors.toSet());
        }

        return authorities;
    }

    public UserEntity userFromId(String id) {
        if (id == null) {
            return null;
        }
        UserEntity user = new UserEntity();
        user.setId(id);
        return user;
    }

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    public UserDTO toDtoId(UserEntity user) {
        if (user == null) {
            return null;
        }
        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        return userDto;
    }

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    public Set<UserDTO> toDtoIdSet(Set<UserEntity> users) {
        if (users == null) {
            return Collections.emptySet();
        }

        Set<UserDTO> userSet = new HashSet<>();
        for (UserEntity userEntity : users) {
            userSet.add(this.toDtoId(userEntity));
        }

        return userSet;
    }

    @Named("login")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    public UserDTO toDtoLogin(UserEntity user) {
        if (user == null) {
            return null;
        }
        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        userDto.setLogin(user.getLogin());
        return userDto;
    }

    @Named("loginSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    public Set<UserDTO> toDtoLoginSet(Set<UserEntity> users) {
        if (users == null) {
            return Collections.emptySet();
        }

        Set<UserDTO> userSet = new HashSet<>();
        for (UserEntity userEntity : users) {
            userSet.add(this.toDtoLogin(userEntity));
        }

        return userSet;
    }
}
