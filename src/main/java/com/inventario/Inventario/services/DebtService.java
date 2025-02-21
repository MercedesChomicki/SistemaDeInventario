package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.DebtRequestDTO;
import com.inventario.Inventario.dtos.DebtResponseDTO;
import com.inventario.Inventario.entities.Customer;
import com.inventario.Inventario.entities.Debt;

import com.inventario.Inventario.entities.Sale;
import com.inventario.Inventario.exceptions.DebtPaidException;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.mappers.DebtMapper;
import com.inventario.Inventario.repositories.DebtRepository;
import com.inventario.Inventario.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DebtService {

    private final DebtRepository debtRepository;
    private final CustomerRepository customerRepository;
    private final DebtMapper debtMapper;

    public List<DebtResponseDTO> getAllDebtsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        List<Debt> debts = debtRepository.findAll(sort);
        return debts.stream()
                .map(debtMapper::toDTO)
                .collect(Collectors.toList());
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

    public Debt payDebt(Integer id, boolean incash, BigDecimal amount) {
        Debt debt = debtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda de cliente", id));

        // Aplicar recargo si no se paga en efectivo
        debt.applySurchargeIfNeeded(incash);

        if(amount.compareTo(debt.getAmountDue()) > 0){
            throw new IllegalArgumentException("El monto ingresado supera a la deuda.");
        }

        if(amount.compareTo(debt.getAmountDue()) == 0) {
            // Deuda saldada
            markSalesAsPaid(debt.getSales());
            deleteDebt(id);
            throw new DebtPaidException("La deuda ha sido completamente pagada y eliminada.");
        }

        // Pago parcial
        debt.processPartialPayment(amount);

        // TODO Agregar el monto pagado (amount) al atributo debtsPaid de la tabla reportes cuando esté

        return debtRepository.save(debt);
    }

    public Debt payDebtWithSurcharge(Integer id, boolean incash, BigDecimal amount) {
        Debt debt = debtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda de cliente", id));

        // Actualizar los precios de los productos

        return debtRepository.save(debt);
    }

    // Método para marcar las ventas asociadas como pagadas
    private void markSalesAsPaid(List<Sale> sales) {
        sales.forEach(Sale::markAsPaid);
    }

    private Map<String, Object> fetchRelatedEntities(DebtRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getCustomerId()));

        Map<String, Object> entities = new HashMap<>();
        entities.put("customer", customer);
        return entities;
    }

    private void deleteDebt(Integer id) {
        try {
            debtRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Deuda de cliente", id);
        }
    }
}
