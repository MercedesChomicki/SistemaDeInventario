package com.inventario.Inventario.mappers;

import com.inventario.Inventario.dtos.CartResponseDTO;
import com.inventario.Inventario.dtos.DetailResponseDTO;
import com.inventario.Inventario.entities.Cart;
import com.inventario.Inventario.entities.CartDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "cart.id", target = "cartId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "details", target = "details") // No es necesario porque MapStruct ya lo hace automáticamente
    @Mapping(target = "total", expression = "java(cart.getDetails().stream()"
            + ".map(d -> d.getSubtotal())"
            + ".reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add))")
    CartResponseDTO toDTO(Cart cart);

    @Mapping(source = "product.id", target = "productId")
    DetailResponseDTO toDetailDTO(CartDetail cartDetail);
}
