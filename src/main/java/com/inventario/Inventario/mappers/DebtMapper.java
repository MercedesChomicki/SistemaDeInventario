package com.inventario.Inventario.mappers;

import com.inventario.Inventario.dtos.DebtResponseDTO;
import com.inventario.Inventario.entities.Debt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DebtMapper {
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "details", target = "details")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "surcharge", target = "surcharge")
    DebtResponseDTO toDTO(Debt debt);
}
