package com.inventario.Inventario.repositories;

import com.inventario.Inventario.entities.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Integer> {

    List<CartProduct> findByCartId(Integer cartId);

    /** Como usuario quiero poder generar un reporte en cualquier momento
     para saber qué productos y qué cantidad de cada uno se va vendiendo en el día,
     con la posibilidad de ordenarlos de menor a mayor o de mayor a menor */
    @Query("SELECT p.name, SUM(cp.quantity), c.creationDate " +
            "FROM CartProduct cp " +
            "JOIN cp.product p " +
            "JOIN cp.cart c " +
            "WHERE c.creationDate BETWEEN :startDate AND :endDate " +
            "GROUP BY p.name ")
    List<Object[]> getSalesReportByDate(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

}
