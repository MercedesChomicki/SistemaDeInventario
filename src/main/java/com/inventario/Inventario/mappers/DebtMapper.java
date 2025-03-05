package com.inventario.Inventario.mappers;

import com.inventario.Inventario.dtos.DebtResponseDTO;
import com.inventario.Inventario.dtos.DetailResponseDTO;
import com.inventario.Inventario.entities.Debt;
import com.inventario.Inventario.entities.DebtDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DebtMapper {
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "details", target = "details")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "surcharge", target = "surcharge")
    DebtResponseDTO toDTO(Debt debt);

    @Mapping(source = "product.id", target = "productId")
    DetailResponseDTO toDetailDTO(DebtDetail debtDetail);
}
