package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.CartProductRequestDTO;
import com.inventario.Inventario.dtos.CartProductResponseDTO;
import com.inventario.Inventario.dtos.CartRequestDTO;
import com.inventario.Inventario.entities.Cart;
import com.inventario.Inventario.entities.CartProduct;
import com.inventario.Inventario.entities.CartProductId;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.CartProductRepository;
import com.inventario.Inventario.repositories.CartRepository;
import com.inventario.Inventario.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final BigDecimal CARD_SURCHARGE_PERCENTAGE = BigDecimal.valueOf(7); // 7% adicional por pago con tarjeta

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;

    public List<Cart> getAllCartsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return cartRepository.findAll(sort);
    }

    public Cart getCartById(Integer id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", id));
    }

    public Cart createCart(CartRequestDTO dto) {
        Cart cart = new Cart();
        cart.setCreationDate(dto.getCreationDate());
        return cartRepository.save(cart);
    }

    public Cart createCart(List<CartProductRequestDTO> cartProducts) {
        if (cartProducts == null || cartProducts.isEmpty()) {
            throw new IllegalArgumentException("No se puede crear un carrito vacío. Debe contener al menos un producto.");
        }

        Cart cart = new Cart();
        cartRepository.save(cart);

        List<CartProduct> cartProductList = cartProducts.stream()
                .map(dto -> new CartProduct(
                        new CartProductId(cart.getId(), dto.getProductId()), // CartProductId id,
                        cart, // Cart cart,
                        productRepository.findById(dto.getProductId()) // Product product,
                                .orElseThrow(() -> new ResourceNotFoundException("Producto", dto.getProductId())),
                        dto.getQuantity(), // int quantity,
                        dto.getPrice() // double price
                ))
                .toList();

        cartProductRepository.saveAll(cartProductList);

        return cart;
    }

    public void deleteCart(Integer id) {
        if (!cartRepository.existsById(id)) {
            throw new ResourceNotFoundException("Carrito", id);
        }
        cartRepository.deleteById(id);
    }

    public List<CartProductResponseDTO> getCartProducts(Integer cartId){
        List<CartProduct> cartProducts = cartProductRepository.findByCartId(cartId);

        return cartProducts.stream()
                .map(cp -> new CartProductResponseDTO(
                        cp.getProduct().getId(),
                        cp.getProduct().getName(),
                        cp.getQuantity(),
                        cp.getPrice()))
                .collect(Collectors.toList());
    }

    public BigDecimal calculateTotalPrice(List<CartProductResponseDTO> cartProducts, boolean isCardPayment) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartProductResponseDTO cartProduct : cartProducts) {
            Integer productId = cartProduct.getProductId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));
            BigDecimal productPrice = isCardPayment ? product.getPriceWithCard(CARD_SURCHARGE_PERCENTAGE) : product.getPrice();
            totalPrice = totalPrice.add(
                    productPrice.multiply(BigDecimal.valueOf(cartProduct.getQuantity())))
                                .setScale(2, RoundingMode.HALF_UP); // Esto asegura que el resultado tenga solo dos decimales.
        }

        return totalPrice;
    }
}
