package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.CartProductRequestDTO;
import com.inventario.Inventario.entities.Cart;
import com.inventario.Inventario.entities.CartProduct;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.exceptions.BusinessException;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.CartProductRepository;
import com.inventario.Inventario.repositories.CartRepository;
import com.inventario.Inventario.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CartProductService {

    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public List<CartProduct> getAllCartProductsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return cartProductRepository.findAll(sort);
    }

    public CartProduct getCartProductById(Integer id) {
        return cartProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", id));
    }

    public CartProduct createCartProduct(CartProductRequestDTO dto) {
        Map<String, Object> entities = fetchRelatedEntities(dto);

        CartProduct cartProduct = new CartProduct();
        cartProduct.setCart((Cart) entities.get("cart"));
        cartProduct.setProduct((Product) entities.get("product"));
        cartProduct.setQuantity(dto.getQuantity());
        cartProduct.setPrice(dto.getPrice());

        return cartProductRepository.save(cartProduct);
    }

    public CartProduct updateCartProduct(Integer id, CartProductRequestDTO updatedCartProduct) {
        CartProduct existingCartProduct  = cartProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", id));

        Map<String, Object> entities = fetchRelatedEntities(updatedCartProduct);

        if (updatedCartProduct.getProductId() != null) existingCartProduct.setProduct((Product) entities.get("product"));

        if (updatedCartProduct.getQuantity() <= 0) throw new BusinessException("La cantidad debe ser mayor a 0.");
        else existingCartProduct.setQuantity(updatedCartProduct.getQuantity());

        if (updatedCartProduct.getPrice() <= 0) throw new BusinessException("El precio debe ser mayor a 0.");
        else existingCartProduct.setPrice(updatedCartProduct.getPrice());

        return cartProductRepository.save(existingCartProduct);
    }

    private Map<String, Object> fetchRelatedEntities(CartProductRequestDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", dto.getProductId()));

        Map<String, Object> entities = new HashMap<>();
        entities.put("product", product);
        return entities;
    }

    public void deleteCartProduct(Integer id) {
        if (!cartProductRepository.existsById(id)) {
            throw new ResourceNotFoundException("Carrito", id);
        }
        cartProductRepository.deleteById(id);
    }

    /** Como usuario quiero poder generar un reporte en cualquier momento
     para saber qué productos y qué cantidad de cada uno se va vendiendo en el día,
     con la posibilidad de ordenarlos de menor a mayor o de mayor a menor
     */
    public List<Object[]> getSalesReportByDate(LocalDate date, boolean asc) {
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(23, 59, 59);

        List<Object[]> results = cartProductRepository.getSalesReportByDate(startDate, endDate);
        results.sort(Comparator.comparing(o -> (Integer) o[1])); // Ordena por cantidad vendida

        if (!asc) Collections.reverse(results); // Si quiere orden descendente, lo invierte

        return results;
    }
}
