package com.inventario.Inventario.repositories;

import com.inventario.Inventario.entities.DebtDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtDetailRepository extends JpaRepository<DebtDetail, Integer> {
}
