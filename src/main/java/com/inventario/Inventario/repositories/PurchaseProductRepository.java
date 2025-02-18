package com.inventario.Inventario.repositories;

import com.inventario.Inventario.entities.PurchaseProduct;
import com.inventario.Inventario.entities.PurchaseProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseProductRepository extends JpaRepository<PurchaseProduct, PurchaseProductId> {

}
