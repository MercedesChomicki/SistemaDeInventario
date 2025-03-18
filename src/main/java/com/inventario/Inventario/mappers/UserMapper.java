package com.inventario.Inventario.mappers;

import com.inventario.Inventario.dtos.UserResponseDTO;
import com.inventario.Inventario.entities.UserEntity;
import com.inventario.Inventario.entities.UserRole;
import com.inventario.Inventario.entities.UserRoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
    @Mapping(target = "registrationTime", source = "registrationTime")
    UserResponseDTO toDTO(UserEntity userEntity);

    @Named("mapRoles")
    default Set<UserRole> mapRoles(Set<UserRoleEntity> roleEntities) {
        return roleEntities.stream()
                .map(UserRoleEntity::getName)
                .collect(Collectors.toSet());
    }
}
