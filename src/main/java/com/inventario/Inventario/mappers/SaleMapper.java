package com.inventario.Inventario.mappers;

import com.inventario.Inventario.dtos.SaleRequestDTO;
import com.inventario.Inventario.dtos.SaleResponseDTO;
import com.inventario.Inventario.entities.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    SaleMapper INSTANCE = Mappers.getMapper(SaleMapper.class);
    SaleResponseDTO toDTO(Sale sale);
    @Mapping(target = "id", ignore = true) // Opcional: Para evitar sobrescribir IDs
    Sale toEntity(SaleRequestDTO dto);
}
