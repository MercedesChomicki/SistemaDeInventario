package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.CartProductRequestDTO;
import com.inventario.Inventario.entities.CartProduct;
import com.inventario.Inventario.services.CartProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart_products")
@RequiredArgsConstructor
public class CartProductController {

    private final CartProductService cartProductService;

    @GetMapping()
    public List<CartProduct> getAllCartProducts(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return cartProductService.getAllCartProductsSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartProduct> getCartProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(cartProductService.getCartProductById(id));
    }

    @PostMapping
    public ResponseEntity<CartProduct> createCartProduct(@RequestBody CartProductRequestDTO cartProductRequestDTO) {
        CartProduct newCartProduct = cartProductService.createCartProduct(cartProductRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCartProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartProduct> updateCartProduct(@PathVariable Integer id, @RequestBody CartProductRequestDTO updatedCartProduct) {
        CartProduct updated = cartProductService.updateCartProduct(id, updatedCartProduct);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCartProduct(@PathVariable Integer id) {
        cartProductService.deleteCartProduct(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }

    /** Como usuario quiero poder generar un reporte en cualquier momento
     para saber qué productos y qué cantidad de cada uno se va vendiendo en el día,
     con la posibilidad de ordenarlos de menor a mayor o de mayor a menor
     */
    @GetMapping("/sales-report")
    public ResponseEntity<List<Map<String, Object>>> getDailySalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "false") boolean asc
    ) {
        List<Object[]> report = cartProductService.getSalesReportByDate(date, asc);

        // Convertir la lista de Object[] a lista de Map<String, Object> para que sea más clara en JSON
        List<Map<String, Object>> response = report.stream().map(obj -> {
            Map<String, Object> item = new HashMap<>();
            item.put("product", obj[0]);
            item.put("quantity", ((Number) obj[1]).longValue());
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
