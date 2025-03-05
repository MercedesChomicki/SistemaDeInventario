package com.inventario.Inventario.services;

import com.inventario.Inventario.entities.Cart;
import com.inventario.Inventario.entities.CartDetail;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.CartRepository;
import com.inventario.Inventario.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartManagerService {

    private final UserService userService;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final StockService stockService;

    public Cart getCartByUserId(Integer userId){
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", userId));
    }

    public Cart getOrCreateCart(Integer userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(userService.getUserById(userId));
                    return cartRepository.save(cart);
                });
    }

    public CartDetail getOrCreateCartDetail(Cart cart, Product product) {
        return cart.getDetails().stream()
                .filter(d -> d.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElseGet(() -> {
                    CartDetail newDetail = new CartDetail(cart, product, 0);
                    cart.getDetails().add(newDetail);
                    return newDetail;});
    }

    public CartDetail getCartDetail(Cart cart, Integer productId){
        return cart.getDetails().stream()
                .filter(d -> d.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));
    }

    public void updateCartDetailQuantity(CartDetail detail, int newQuantity) {
        if (detail.getQuantity() != newQuantity) {
            detail.setQuantity(newQuantity);
            detail.setSubtotal(detail.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));
        }
    }

    public Product getProduct(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));
    }

    public void validateAndUpdateStock(Product product, int newQuantity, int oldQuantity) {
        stockService.validateAndUpdateStock(product, newQuantity, oldQuantity);
    }

    public Cart addToCart(Integer userId, Integer productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = getProduct(productId);
        validateAndUpdateStock(product, quantity, 0);
        CartDetail detail = getOrCreateCartDetail(cart, product);
        updateCartDetailQuantity(detail, detail.getQuantity() + quantity);
        return cartRepository.save(cart);
    }

    /**
     * Delta significa el cambio en 𝑥 entre un estado inicial y un estado final.
     */
    public void changeQuantity(Integer userId, Integer productId, int delta) {
        Cart cart = getCartByUserId(userId);
        CartDetail detail = getCartDetail(cart, productId);
        int newQuantity = detail.getQuantity() + delta;

        if (newQuantity <= 0)
            throw new IllegalArgumentException("La cantidad mínima es 1");

        validateAndUpdateStock(detail.getProduct(), newQuantity, detail.getQuantity());
        updateCartDetailQuantity(detail, newQuantity);

        cartRepository.save(cart);
    }

    public void clearCart(Integer userId) {
        Cart cart = getCartByUserId(userId);
        cart.getDetails().clear();
        cartRepository.save(cart);
    }

    public void removeItems(Integer userId, List<Integer> productIds) {
        Cart cart = getCartByUserId(userId);

        if (cart.getDetails().isEmpty())
            throw new IllegalArgumentException("El carrito ya está vacío");

        cart.getDetails().removeIf(detail -> {
            if (productIds.contains(detail.getProduct().getId())) {
                validateAndUpdateStock(detail.getProduct(), 0, detail.getQuantity());
                return true;
            }
            return false;
        });

        cartRepository.save(cart);
    }

}
