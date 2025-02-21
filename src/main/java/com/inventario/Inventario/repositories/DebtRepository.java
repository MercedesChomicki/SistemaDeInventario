package com.inventario.Inventario.repositories;

import com.inventario.Inventario.entities.Customer;
import com.inventario.Inventario.entities.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Integer> {
    Optional<Debt> findByCustomer(Customer customer);
}
