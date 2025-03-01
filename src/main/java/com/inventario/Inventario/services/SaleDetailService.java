package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.SalesReportDTO;
import com.inventario.Inventario.repositories.SaleDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SaleDetailService {

    private final SaleDetailRepository saleDetailRepository;

    /** Como usuario quiero poder generar un reporte en cualquier momento
     para saber qué productos y qué cantidad de cada uno se va vendiendo en el día,
     con la posibilidad de ordenarlos de menor a mayor o de mayor a menor
     */
    public List<SalesReportDTO> getSalesReportByDate(LocalDate date, boolean asc) {
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(23, 59, 59);
        List<SalesReportDTO> results = saleDetailRepository.getSalesReportByDate(startDate, endDate);

        results.sort(Comparator.comparing(SalesReportDTO::getQuantity)); // Orden ascendente
        if (!asc) Collections.reverse(results); // Si es descendente, se invierte

        return results;
    }
}
