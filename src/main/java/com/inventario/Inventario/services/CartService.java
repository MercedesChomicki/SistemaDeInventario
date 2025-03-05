package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.CartResponseDTO;
import com.inventario.Inventario.entities.Cart;
import com.inventario.Inventario.mappers.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica del carrito
 * 1. Cart:
 * - Relacionada con CartDetail (similar a Sale y SaleDetail).
 * - Solo almacena temporalmente los productos agregados.

 * 2. Cuando el usuario agrega productos al carrito:
 * - Se crea un registro en CartDetail.
 * - Se asocia al Cart del usuario.
 * - Si el producto ya está en el carrito, solo se actualiza la cantidad.

 * Cuando el usuario finaliza la compra:
 * - Se convierten los CartDetail en SaleDetail.
 * - Se guarda la venta (Sale).
 * - Se eliminan los registros de CartDetail.
 */

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartMapper cartMapper;
    private final CartManagerService cartManagerService;

    public CartResponseDTO getCartByUserId(Integer userId) {
        Cart cart = cartManagerService.getCartByUserId(userId);
        return cartMapper.toDTO(cart);
    }

    @Transactional
    public CartResponseDTO addToCart(Integer userId, Integer productId, Integer quantity) {
        if(quantity <= 0)
            throw new IllegalArgumentException("La cantidad minima es 1");

        Cart cart = cartManagerService.addToCart(userId, productId, quantity);
        return cartMapper.toDTO(cart);
    }

    @Transactional
    public void updateProductQuantity(Integer userId, Integer productId, int newQuantity, int oldQuantity) {
        cartManagerService.changeQuantity(userId, productId, (newQuantity-oldQuantity));
    }

    @Transactional
    public void decreaseQuantity(Integer userId, Integer productId) {
        cartManagerService.changeQuantity(userId, productId, -1);
    }

    @Transactional
    public void increaseQuantity(Integer userId, Integer productId) {
        cartManagerService.changeQuantity(userId, productId, +1);
    }

    @Transactional
    public void clearCart(Integer userId) {
        cartManagerService.clearCart(userId);
    }

    @Transactional
    public void removeItem(Integer userId, Integer productId) {
        cartManagerService.removeItems(userId, List.of(productId));
    }

    @Transactional
    public void removeAllItems(Integer userId) {
        Cart cart = cartManagerService.getCartByUserId(userId);
        List<Integer> productIds = cart.getDetails().stream()
                .map(detail -> detail.getProduct().getId())
                .toList();
        cartManagerService.removeItems(userId, productIds);
    }
}