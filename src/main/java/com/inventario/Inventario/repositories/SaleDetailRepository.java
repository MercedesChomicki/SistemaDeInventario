package com.inventario.Inventario.repositories;

import com.inventario.Inventario.dtos.SalesReportDTO;
import com.inventario.Inventario.entities.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleDetailRepository extends JpaRepository<TransactionDetail, Long> {

    /** Como usuario quiero poder generar un reporte en cualquier momento
     para saber qué productos y qué cantidad de cada uno se va vendiendo en el día,
     con la posibilidad de ordenarlos de menor a mayor o de mayor a menor */
    @Query("""
    SELECT sd.product.name, SUM(sd.quantity), s.date
    FROM SaleDetail sd JOIN sd.sale s
    WHERE s.date BETWEEN :startDate AND :endDate
    GROUP BY sd.product.name, s.date
   """)
    List<SalesReportDTO> getSalesReportByDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
