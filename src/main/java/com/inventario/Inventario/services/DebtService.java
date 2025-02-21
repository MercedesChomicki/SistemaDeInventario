package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.DebtRequestDTO;
import com.inventario.Inventario.dtos.DebtResponseDTO;
import com.inventario.Inventario.entities.Customer;
import com.inventario.Inventario.entities.Debt;

import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.DebtRepository;
import com.inventario.Inventario.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DebtService {

    private final DebtRepository debtRepository;
    private final CustomerRepository customerRepository;

    public List<DebtResponseDTO> getAllDebtsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        List<Debt> debts = debtRepository.findAll(sort);
        ArrayList<DebtResponseDTO> listDTO = new ArrayList<>();
        for(Debt debt : debts){
            DebtResponseDTO dto= convertToDTO(debt);
            listDTO.add(dto);
        }
        return listDTO;
    }

    private DebtResponseDTO convertToDTO(Debt debt) {
        DebtResponseDTO dto = new DebtResponseDTO();
        dto.setId(debt.getId());
        dto.setCustomerId(debt.getCustomer().getId());
        dto.setAmountTotal(debt.getAmountTotal());
        dto.setAmountDue(debt.getAmountDue());
        dto.setAmountPaid(debt.getAmountPaid());
        dto.setCreatedAt(debt.getCreatedAt());
        dto.setUpdatedAt(debt.getUpdatedAt());
        return dto;
    }

    public Debt getDebtById(Integer id) {
        return debtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda de cliente", id));
    }

    public Debt createDebt(DebtRequestDTO dto) {
        Map<String, Object> entities = fetchRelatedEntities(dto);

        Debt debt = new Debt();
        debt.setCustomer((Customer) entities.get("customer"));

        return debtRepository.save(debt);
    }

    public Debt updateDebt(Integer id, DebtRequestDTO updatedDebt) {
        Debt existingDebt = debtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda de cliente", id));

        Map<String, Object> entities = fetchRelatedEntities(updatedDebt);


        return debtRepository.save(existingDebt);
    }

    private Map<String, Object> fetchRelatedEntities(DebtRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getCustomerId()));

        Map<String, Object> entities = new HashMap<>();
        entities.put("customer", customer);
        return entities;
    }

    private void deleteDebt(Integer id) {
        if (!debtRepository.existsById(id)) {
            throw new ResourceNotFoundException("Deuda de cliente", id);
        }
        debtRepository.deleteById(id);
    }
}
