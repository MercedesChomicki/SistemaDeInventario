package com.inventario.Inventario.repositories;

import com.inventario.Inventario.entities.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtDetailRepository extends JpaRepository<TransactionDetail, Long> {
}
