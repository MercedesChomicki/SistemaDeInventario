package com.inventario.Inventario.mappers;

import com.inventario.Inventario.dtos.ProductFullResponseDTO;
import com.inventario.Inventario.dtos.ProductResponseDTO;
import com.inventario.Inventario.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "salePrice", target = "salePrice") // Mapea cashPrice a price
    @Mapping(source = "species.name", target = "species") // Extrae el nombre de la especie
    @Mapping(source = "category.name", target = "category") // Extrae el nombre de la categoría
    @Mapping(target = "supplier", expression = "java(product.getSupplier().getBusinessName() != null ? product.getSupplier().getBusinessName() : product.getSupplier().getSellerName())")
    ProductResponseDTO toDTO(Product product);

    @Mapping(source = "cost", target = "cost")
    @Mapping(source = "salePrice", target = "salePrice")
    @Mapping(source = "species.name", target = "species") // Extrae el nombre de la especie
    @Mapping(source = "category.name", target = "category") // Extrae el nombre de la categoría
    @Mapping(target = "supplier", expression = "java(product.getSupplier().getBusinessName() != null ? product.getSupplier().getBusinessName() : product.getSupplier().getSellerName())")
    ProductFullResponseDTO toFullDTO(Product product);
}
