package com.inventario.Inventario.repositories;

import com.inventario.Inventario.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministratorRepository extends JpaRepository<Admin, Integer> {
}
