package com.inventario.Inventario.repositories;

import com.inventario.Inventario.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE UPPER(p.code) = UPPER(:code)")
    boolean existsByCodeIgnoreCase(String code);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE UPPER(p.name) = UPPER(:name)")
    boolean existsByNameIgnoreCase(String name);
}

