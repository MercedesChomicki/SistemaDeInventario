package com.inventario.Inventario.mappers;

import com.inventario.Inventario.dtos.ProductFullResponseDTO;
import com.inventario.Inventario.dtos.ProductRequestDTO;
import com.inventario.Inventario.dtos.ProductResponseDTO;
import com.inventario.Inventario.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "price", target = "price") // Mapea cashPrice a price
    @Mapping(source = "species.name", target = "species") // Extrae el nombre de la especie
    @Mapping(source = "category.name", target = "category") // Extrae el nombre de la categoría
    @Mapping(target = "supplier",  expression = "java(product.getSupplier().getSellerName())") // Extrae el nombre del proveedor
    ProductResponseDTO toDTO(Product product);

    @Mapping(target = "id", ignore = true) // Opcional: Para evitar sobrescribir IDs
    Product toEntity(ProductRequestDTO dto);

    @Mapping(source = "species.name", target = "species") // Extrae el nombre de la especie
    @Mapping(source = "category.name", target = "category") // Extrae el nombre de la categoría
    @Mapping(target = "supplier",  expression = "java(product.getSupplier().getSellerName())")
    ProductFullResponseDTO toFullDTO(Product product);
}
