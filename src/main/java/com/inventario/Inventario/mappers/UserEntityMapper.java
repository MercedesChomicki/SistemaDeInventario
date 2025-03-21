package com.inventario.Inventario.mappers;

import com.inventario.Inventario.dtos.UserResponseDTO;
import com.inventario.Inventario.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    @Mapping(target = "role", source = "role")
    @Mapping(target = "registrationTime", source = "registrationTime")
    UserResponseDTO toDTO(UserEntity userEntity);

    @Mapping(target = "role", source = "role")
    @Mapping(target = "registrationTime", source = "registrationTime")
    UserEntity toEntity(UserResponseDTO userResponseDTO);
}
