package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.SaleRequestDTO;
import com.inventario.Inventario.dtos.SaleResponseDTO;
import com.inventario.Inventario.entities.Sale;
import com.inventario.Inventario.services.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @GetMapping()
    public List<SaleResponseDTO> getAllSales(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return saleService.getAllSalesSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @PostMapping
    public ResponseEntity<SaleResponseDTO> createSale(@RequestBody SaleRequestDTO dto) {
        SaleResponseDTO newSale = saleService.createSale(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSale);
    }

    /*@GetMapping("/{saleId}/products")
    public List<SaleProductResponseDTO> getSaleProducts(@PathVariable Integer saleId) {
        return saleService.getSaleProducts(saleId);
    }

    @PostMapping("/{saleId}/checkout")
    public ResponseEntity<BigDecimal> checkout(@PathVariable Integer saleId, @RequestParam boolean isCardPayment) {
        // Obtener el carrito y sus productos
        List<SaleProductResponseDTO> saleProducts = saleService.getSaleProducts(saleId);
        BigDecimal totalPrice = saleService.calculateTotalPrice(saleProducts, isCardPayment);
        return ResponseEntity.ok(totalPrice);
    }*/
}
