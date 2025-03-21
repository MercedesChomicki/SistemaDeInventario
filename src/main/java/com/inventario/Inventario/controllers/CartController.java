package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.CartRequestDTO;
import com.inventario.Inventario.dtos.CartResponseDTO;
import com.inventario.Inventario.dtos.CartUptRequestDTO;
import com.inventario.Inventario.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable Integer userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(@RequestBody CartRequestDTO request) {
        CartResponseDTO dto = cartService.addToCart(request.getUserId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> updateCartUser(@PathVariable Integer userId,
                                                      @RequestBody CartUptRequestDTO request) {
        CartResponseDTO dto = cartService.updateCartUser(userId, request);
        return ResponseEntity.ok(dto);
    }

    /**
     * @param oldQuantity Es la cantidad que ya tenía agregada al carrito
     */
    @PutMapping("/{userId}/update/{productId}")
    public ResponseEntity<String> updateQuantity(@PathVariable Integer userId, @PathVariable Integer productId, @RequestParam int newQuantity, @RequestParam int oldQuantity) {
        cartService.updateProductQuantity(userId, productId, newQuantity, oldQuantity);
        return ResponseEntity.ok("El producto "+productId+" se ha actualizado exitosamente");
    }

    @PutMapping("/{userId}/decrease/{productId}")
    public ResponseEntity<String> decreaseQuantity(@PathVariable Integer userId, @PathVariable Integer productId) {
        cartService.decreaseQuantity(userId, productId);
        return ResponseEntity.ok("El producto "+productId+" se ha decrementado exitosamente");
    }

    @PutMapping("/{userId}/increase/{productId}")
    public ResponseEntity<String> increaseQuantity(@PathVariable Integer userId, @PathVariable Integer productId) {
        cartService.increaseQuantity(userId, productId);
        return ResponseEntity.ok("El producto "+productId+" se ha incrementado exitosamente");
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<String> removeItem(@PathVariable Integer userId, @PathVariable Integer productId) {
        cartService.removeItem(userId, productId);
        return ResponseEntity.ok("El producto "+productId+" se ha eliminado exitosamente");
    }

    @DeleteMapping("/{userId}/remove-all-items")
    public ResponseEntity<String> removeAllItems(@PathVariable Integer userId) {
        cartService.removeAllItems(userId);
        return ResponseEntity.ok("Carrito vaciado.");
    }
}
