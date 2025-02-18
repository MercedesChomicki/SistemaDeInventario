package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.AlertRequestDTO;
import com.inventario.Inventario.entities.Alert;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.AlertRepository;
import com.inventario.Inventario.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final ProductRepository productRepository;

    public List<Alert> getAllAlertsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return alertRepository.findAll(sort);
    }

    public Alert getAlertById(Integer id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta", id));
    }

    public Alert createAlert(AlertRequestDTO dto) {
        Map<String, Object> entities = fetchRelatedEntities(dto);

        Alert alert = new Alert();
        alert.setProduct((Product) entities.get("product"));
        alert.setType(dto.getAlertType());
        alert.setDate(dto.getDate());

        return alertRepository.save(alert);
    }

    public Alert updateAlert(Integer id, AlertRequestDTO updatedAlert) {
        Alert existingAlert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta",id));

        Map<String, Object> entities = fetchRelatedEntities(updatedAlert);

        if(updatedAlert.getProductId() != null) existingAlert.setProduct((Product) entities.get("product"));
        if(updatedAlert.getAlertType() != null) existingAlert.setType(updatedAlert.getAlertType());
        if(updatedAlert.getDate() != null) existingAlert.setDate(updatedAlert.getDate());

        return alertRepository.save(existingAlert);
    }

    private Map<String, Object> fetchRelatedEntities(AlertRequestDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", dto.getProductId()));

        Map<String, Object> entities = new HashMap<>();
        entities.put("product", product);
        return entities;
    }

    public void deleteAlert(Integer id) {
        if (!alertRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alerta", id);
        }
        alertRepository.deleteById(id);
    }

}
