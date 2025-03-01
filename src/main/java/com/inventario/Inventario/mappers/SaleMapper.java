package com.inventario.Inventario.mappers;

import com.inventario.Inventario.dtos.SaleDetailResponseDTO;
import com.inventario.Inventario.dtos.SaleRequestDTO;
import com.inventario.Inventario.dtos.SaleResponseDTO;
import com.inventario.Inventario.entities.Sale;
import com.inventario.Inventario.entities.SaleDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    SaleMapper INSTANCE = Mappers.getMapper(SaleMapper.class);

    @Mapping(source = "payments", target = "payments")
    @Mapping(source = "details", target = "details")
    SaleResponseDTO toDTO(Sale sale);

    @Mapping(source = "product.id", target = "productId")
    SaleDetailResponseDTO toDetailDTO(SaleDetail saleDetail);

    @Mapping(target = "id", ignore = true) // Opcional: Para evitar sobrescribir IDs
    Sale toEntity(SaleRequestDTO dto);
}
