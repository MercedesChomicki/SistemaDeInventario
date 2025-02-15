package com.inventario.Inventario.repositories;

import com.inventario.Inventario.entities.CloudinaryAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CloudinaryAccountRepository extends JpaRepository<CloudinaryAccount, UUID> {
}
