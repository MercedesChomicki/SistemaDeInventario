package com.inventario.Inventario.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SalesReportDTO {
    private String product;
    private Long quantity;
    private LocalDate date;
}
