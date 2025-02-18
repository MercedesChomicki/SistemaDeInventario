package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.CartProductRequestDTO;
import com.inventario.Inventario.dtos.CartProductResponseDTO;
import com.inventario.Inventario.entities.Cart;
import com.inventario.Inventario.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping()
    public List<Cart> getAllCarts(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return cartService.getAllCartsSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable Integer id) {
        return ResponseEntity.ok(cartService.getCartById(id));
    }

    @PostMapping
    public ResponseEntity<Cart> createCart(@RequestBody List<CartProductRequestDTO> cartProducts) {
        Cart newCart = cartService.createCart(cartProducts);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCart);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCart(@PathVariable Integer id) {
        cartService.deleteCart(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }

    @GetMapping("/{cartId}/products")
    public List<CartProductResponseDTO> getCartProducts(@PathVariable Integer cartId) {
        return cartService.getCartProducts(cartId);
    }

    @PostMapping("/{cartId}/checkout")
    public ResponseEntity<BigDecimal> checkout(@PathVariable Integer cartId, @RequestParam boolean isCardPayment) {
        // Obtener el carrito y sus productos
        List<CartProductResponseDTO> cartProducts = cartService.getCartProducts(cartId);
        BigDecimal totalPrice = cartService.calculateTotalPrice(cartProducts, isCardPayment);
        return ResponseEntity.ok(totalPrice);
    }
}
