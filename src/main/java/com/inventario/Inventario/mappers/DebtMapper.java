package com.inventario.Inventario.mappers;

import com.inventario.Inventario.dtos.DebtDetailResponseDTO;
import com.inventario.Inventario.dtos.DebtResponseDTO;
import com.inventario.Inventario.entities.Debt;
import com.inventario.Inventario.entities.DebtDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DebtMapper {
    DebtMapper INSTANCE = Mappers.getMapper(DebtMapper.class);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "details", target = "details")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "surcharge", target = "surcharge")
    DebtResponseDTO toDTO(Debt debt);

    @Mapping(source = "product.id", target = "productId") // Extrae el ID del producto
    DebtDetailResponseDTO toDTO(DebtDetail debtDetail);

    @Mapping(target = "id", ignore = true) // Opcional: Para evitar sobrescribir IDs
    Debt toEntity(DebtResponseDTO dto);
}
