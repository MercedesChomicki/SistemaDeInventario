package com.inventario.Inventario.mappers;

import com.inventario.Inventario.dtos.DebtRequestDTO;
import com.inventario.Inventario.dtos.DebtResponseDTO;
import com.inventario.Inventario.entities.Debt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DebtMapper {
    DebtMapper INSTANCE = Mappers.getMapper(DebtMapper.class);
    DebtResponseDTO toDTO(Debt debt);
    @Mapping(target = "id", ignore = true) // Opcional: Para evitar sobrescribir IDs
    Debt toEntity(DebtRequestDTO dto);
}
