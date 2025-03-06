package com.inventario.Inventario.mappers;

import com.inventario.Inventario.dtos.DetailResponseDTO;
import com.inventario.Inventario.dtos.PurchaseResponseDTO;
import com.inventario.Inventario.entities.Purchase;
import com.inventario.Inventario.entities.PurchaseDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {
    @Mapping(source = "surcharge", target = "surcharge")
    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "payments", target = "payments")
    @Mapping(source = "details", target = "details", qualifiedByName = "mapDetails")
    PurchaseResponseDTO toDTO(Purchase purchase);

    @Mapping(source = "product.id", target = "productId")
    DetailResponseDTO toDetailDTO(PurchaseDetail purchaseDetail);

    @Named("mapDetails")
    default List<DetailResponseDTO> mapDetails(List<PurchaseDetail> details) {
        if (details == null) {
            return Collections.emptyList();
        }
        return details.stream()
                .map(this::toDetailDTO)
                .collect(Collectors.toList());
    }

}
