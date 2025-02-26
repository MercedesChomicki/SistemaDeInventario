package com.inventario.Inventario.repositories;

import com.inventario.Inventario.entities.Customer;
import com.inventario.Inventario.entities.Debt;
import com.inventario.Inventario.entities.DebtStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Integer> {
    Debt findByCustomerAndStatusIn(Customer customer, List<DebtStatus> statuses);
}
