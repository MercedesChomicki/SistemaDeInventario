package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.CartResponseDTO;
import com.inventario.Inventario.entities.Cart;
import com.inventario.Inventario.entities.CartDetail;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.mappers.CartMapper;
import com.inventario.Inventario.repositories.CartRepository;
import com.inventario.Inventario.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

    private final CartRepository cartRepository;
    private final UserService userService;
    private final StockService stockService;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;

    public CartResponseDTO getCartByUserId(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", userId));
        return cartMapper.toDTO(cart);
    }

    @Transactional
    public CartResponseDTO addToCart(Integer userId, Integer productId, Integer quantity) {
        if(quantity <= 0)
            throw new IllegalArgumentException("La cantidad minima es 1");

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));

        stockService.validateStock(product, quantity);
        stockService.updateAndSaveStock(product, quantity, 0);

        CartDetail detail = findOrCreateCartDetail(cart, product);
        detail.setQuantity(detail.getQuantity() + quantity);
        detail.setSubtotal(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));

        cartRepository.save(cart);
        return cartMapper.toDTO(cart);
    }

    @Transactional
    public CartResponseDTO updateProductQuantity(Integer userId, Integer productId, int quantity, int selectedQuantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));

        stockService.validateStock(product, quantity);
        stockService.updateAndSaveStock(product, quantity, selectedQuantity);

        CartDetail detail = cart.getDetails().stream()
                .filter(d -> d.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));

        detail.setQuantity(quantity);
        detail.setSubtotal(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));

        cartRepository.save(cart);
        return cartMapper.toDTO(cart);
    }

    @Transactional
    public void decreaseQuantity(Integer userId, Integer productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));

        CartDetail detail = cart.getDetails().stream()
                .filter(d -> d.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));

        if (detail.getQuantity() == 1)
            throw new IllegalArgumentException("La cantidad minima es 1");

        detail.setQuantity(detail.getQuantity()-1);
        detail.setSubtotal(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
        stockService.increaseAndSaveStock(detail.getProduct());

        cartRepository.save(cart);
    }

    @Transactional
    public void increaseQuantity(Integer userId, Integer productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));

        stockService.validateStock(product);

        CartDetail detail = cart.getDetails().stream()
                .filter(d -> d.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));

        detail.setQuantity(detail.getQuantity()+1);
        detail.setSubtotal(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
        stockService.decreaseAndSaveStock(product);

        cartRepository.save(cart);
    }

    public void removeItem(Integer userId, Integer productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));

        CartDetail detail = cart.getDetails().stream()
                .filter(d -> d.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));

        stockService.updateAndSaveStock(detail.getProduct(), 0, detail.getQuantity());

        cart.getDetails().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }

    private CartDetail findOrCreateCartDetail(Cart cart, Product product) {
        return cart.getDetails().stream()
                .filter(d -> d.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElseGet(() -> {
                    CartDetail newDetail = new CartDetail(cart, product, 0);
                    cart.getDetails().add(newDetail);
                    return newDetail;
                });
    }

    public void clearCart(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));
        for(CartDetail detail : cart.getDetails()) {
            Product product = detail.getProduct();
            stockService.updateAndSaveStock(product, 0, detail.getQuantity());
        }
        cart.getDetails().clear();
        cartRepository.save(cart);
    }

    private Cart createCart(Integer userId) {
        Cart cart = new Cart();
        cart.setUser(userService.getUserById(userId));
        return cartRepository.save(cart);
    }
}
