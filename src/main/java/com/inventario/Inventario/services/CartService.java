package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.CartRequestDTO;
import com.inventario.Inventario.entities.Cart;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

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

    public void deleteCart(Integer id) {
        if (!cartRepository.existsById(id)) {
            throw new ResourceNotFoundException("Carrito", id);
        }
        cartRepository.deleteById(id);
    }
}
