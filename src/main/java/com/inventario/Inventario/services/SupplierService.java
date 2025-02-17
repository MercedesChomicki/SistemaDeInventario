package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.SupplierRequestDTO;
import com.inventario.Inventario.entities.Supplier;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliersSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return supplierRepository.findAll(sort);
    }

    public Supplier getSupplierById(Integer id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
    }

    public Supplier createSupplier(SupplierRequestDTO dto) {
        Supplier supplier = new Supplier();
        supplier.setFirstname(dto.getFirstname());
        supplier.setLastname(dto.getLastname());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        supplier.setCompany(dto.getCompany());
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Integer id, SupplierRequestDTO updatedSupplier) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor",id));
        if(updatedSupplier.getFirstname() != null) existingSupplier.setFirstname(updatedSupplier.getFirstname());
        if(updatedSupplier.getLastname() != null) existingSupplier.setLastname(updatedSupplier.getLastname());
        if(updatedSupplier.getPhone() != null) existingSupplier.setPhone(updatedSupplier.getPhone());
        if(updatedSupplier.getEmail() != null) existingSupplier.setEmail(updatedSupplier.getEmail());
        if(updatedSupplier.getCompany() != null) existingSupplier.setCompany(updatedSupplier.getCompany());

        return supplierRepository.save(existingSupplier);
    }

    public void deleteSupplier(Integer id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor", id);
        }
        supplierRepository.deleteById(id);
    }

}
