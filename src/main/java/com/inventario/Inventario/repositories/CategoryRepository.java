package com.inventario.Inventario.repositories;

import com.inventario.Inventario.entities.Category;
import com.inventario.Inventario.entities.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
