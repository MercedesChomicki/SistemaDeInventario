package com.inventario.Inventario.repositories;

import com.inventario.Inventario.entities.PurchaseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseDetailRepository extends JpaRepository<PurchaseDetail, Integer> {
}
