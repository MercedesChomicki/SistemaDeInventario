package com.inventario.Inventario.services;

import com.inventario.Inventario.entities.Supplier;
import com.inventario.Inventario.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliersSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return supplierRepository.findAll(sort);
    }

    public Optional<Supplier> getSupplierById(Integer id) {
        return supplierRepository.findById(id);
    }

    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Optional<Supplier> updateSupplier(Integer id, Supplier updatedSupplier) {
        return supplierRepository.findById(id).map(existingSupplier -> {
            existingSupplier.setFirstname(updatedSupplier.getFirstname());
            existingSupplier.setLastname(updatedSupplier.getLastname());
            existingSupplier.setEmail(updatedSupplier.getEmail());
            existingSupplier.setMobileNumber(updatedSupplier.getMobileNumber());
            existingSupplier.setCompany(updatedSupplier.getCompany());
            return supplierRepository.save(existingSupplier);
        });
    }

    public boolean deleteSupplier(Integer id) {
        if (supplierRepository.existsById(id)) {
            supplierRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
