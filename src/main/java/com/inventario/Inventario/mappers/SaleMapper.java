package com.inventario.Inventario.mappers;

import com.inventario.Inventario.dtos.DetailResponseDTO;
import com.inventario.Inventario.dtos.SaleResponseDTO;
import com.inventario.Inventario.entities.Sale;
import com.inventario.Inventario.entities.SaleDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    @Mapping(source = "payments", target = "payments")
    @Mapping(source = "details", target = "details")
    SaleResponseDTO toDTO(Sale sale);

    @Mapping(source = "product.id", target = "productId")
    DetailResponseDTO toDetailDTO(SaleDetail saleDetail);
}
