package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.SaleDetailRequestDTO;
import com.inventario.Inventario.entities.SaleDetail;
import com.inventario.Inventario.services.SaleDetailService;
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
@RequestMapping("/sale_details")
@RequiredArgsConstructor
public class SaleDetailController {

    private final SaleDetailService saleDetailService;

    @GetMapping()
    public List<SaleDetail> getAllSaleDetails(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return saleDetailService.getAllSaleDetailsSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleDetail> getSaleDetailById(@PathVariable Long id) {
        return ResponseEntity.ok(saleDetailService.getSaleDetailById(id));
    }

    @PostMapping
    public ResponseEntity<SaleDetail> createSaleDetail(@RequestBody SaleDetailRequestDTO saleDetailRequestDTO) {
        SaleDetail newSaleDetail = saleDetailService.createSaleDetail(saleDetailRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSaleDetail);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleDetail> updateSaleDetail(@PathVariable Long id, @RequestBody SaleDetailRequestDTO saleDetailRequestDTO) {
        SaleDetail updated = saleDetailService.updateSaleDetail(id, saleDetailRequestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSaleDetail(@PathVariable Long id) {
        saleDetailService.deleteSaleDetail(id);
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
        List<Object[]> report = saleDetailService.getSalesReportByDate(date, asc);

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
