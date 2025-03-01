package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.SalesReportDTO;
import com.inventario.Inventario.services.SaleDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/sale_details")
@RequiredArgsConstructor
public class ReportController {

    private final SaleDetailService saleDetailService;

    /** Como usuario quiero poder generar un reporte en cualquier momento
     para saber qué productos y qué cantidad de cada uno se va vendiendo en el día,
     con la posibilidad de ordenarlos de menor a mayor o de mayor a menor
     */
    @GetMapping("/sales-report")
    public ResponseEntity<List<SalesReportDTO>> getDailySalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "false") boolean asc
    ) {
        return ResponseEntity.ok(saleDetailService.getSalesReportByDate(date, asc));
    }
}
