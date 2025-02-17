package com.inventario.Inventario.repositories;

import com.inventario.Inventario.entities.CustomerDebt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerDebtRepository extends JpaRepository<CustomerDebt, Integer> {
}
