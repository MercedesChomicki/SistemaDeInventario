package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.SupplierRequestDTO;
import com.inventario.Inventario.entities.Supplier;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

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
        Supplier supplier = new Supplier(dto.getDocumentType(), dto.getDocumentNumber(),
                dto.getBusinessName(), dto.getSellerName(), dto.getTaxCategory(),
                dto.getLegalEntity(), dto.getCountry(), dto.getProvince(), dto.getCity(),
                dto.getAddress(), dto.getFloorOrApartment(), dto.getPostalCode(),
                dto.getPhone(), dto.getEmail());

        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Integer id, SupplierRequestDTO updatedSupplier) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor",id));

        if(updatedSupplier.getDocumentType() != null) existingSupplier.setDocumentType(updatedSupplier.getDocumentType());
        if(updatedSupplier.getDocumentNumber() != null) existingSupplier.setDocumentNumber(updatedSupplier.getDocumentNumber());
        if(updatedSupplier.getBusinessName() != null) existingSupplier.setBusinessName(updatedSupplier.getBusinessName());
        if(updatedSupplier.getSellerName() != null) existingSupplier.setSellerName(updatedSupplier.getSellerName());
        if(updatedSupplier.getTaxCategory() != null) existingSupplier.setTaxCategory(updatedSupplier.getTaxCategory());
        if (updatedSupplier.getLegalEntity() != null) existingSupplier.setLegalEntity(updatedSupplier.getLegalEntity());
        if(updatedSupplier.getCountry() != null) existingSupplier.setCountry(updatedSupplier.getCountry());
        if(updatedSupplier.getProvince() != null) existingSupplier.setProvince(updatedSupplier.getProvince());
        if(updatedSupplier.getCity() != null) existingSupplier.setCity(updatedSupplier.getCity());
        if(updatedSupplier.getAddress() != null) existingSupplier.setAddress(updatedSupplier.getAddress());
        if(updatedSupplier.getFloorOrApartment() != null) existingSupplier.setFloorOrApartment(updatedSupplier.getFloorOrApartment());
        if(updatedSupplier.getPostalCode() != null) existingSupplier.setPostalCode(updatedSupplier.getPostalCode());
        if(updatedSupplier.getPhone() != null) existingSupplier.setPhone(updatedSupplier.getPhone());
        if(updatedSupplier.getEmail() != null) existingSupplier.setEmail(updatedSupplier.getEmail());

        return supplierRepository.save(existingSupplier);
    }

    public void deleteSupplier(Integer id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor", id);
        }
        supplierRepository.deleteById(id);
    }
}
