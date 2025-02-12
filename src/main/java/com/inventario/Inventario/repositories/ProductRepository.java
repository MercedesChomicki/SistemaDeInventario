package com.inventario.Inventario.repositories;

import com.inventario.Inventario.entities.Category;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.entities.Species;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    /* Como usuario quiero poder filtrar productos por nombre, categoría
    o especie de animal (gato/perro) */

    List<Product> findByName(String name);
    List<Product> findByCategory(Category category);
    List<Product> findBySpecies(Species species);

    /* Como usuario quiero poder ordenar los productos por nombre, precio, stock */
    List<Product> findBy(Sort sort);
}

