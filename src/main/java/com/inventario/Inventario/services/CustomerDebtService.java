package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.CustomerDebtRequestDTO;
import com.inventario.Inventario.entities.Customer;
import com.inventario.Inventario.entities.CustomerDebt;
import com.inventario.Inventario.exceptions.BusinessException;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.CustomerDebtRepository;
import com.inventario.Inventario.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerDebtService {

    private final CustomerDebtRepository customerDebtRepository;
    private final CustomerRepository customerRepository;

    public List<CustomerDebt> getAllCustomerDebtsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return customerDebtRepository.findAll(sort);
    }

    public CustomerDebt getCustomerDebtById(Integer id) {
        return customerDebtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda de cliente", id));
    }

    public CustomerDebt createCustomerDebt(CustomerDebtRequestDTO dto) {
        Map<String, Object> entities = fetchRelatedEntities(dto);

        CustomerDebt customerDebt = new CustomerDebt();
        customerDebt.setCustomer((Customer) entities.get("customer"));
        customerDebt.setDate(dto.getDate());
        customerDebt.setTotalAmount(dto.getTotalAmount());
        customerDebt.setAmountPaid(dto.getAmountPaid());

        return customerDebtRepository.save(customerDebt);
    }

    public CustomerDebt updateCustomerDebt(Integer id, CustomerDebtRequestDTO updatedCustomerDebt) {
        CustomerDebt existingCustomerDebt = customerDebtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda de cliente", id));

        Map<String, Object> entities = fetchRelatedEntities(updatedCustomerDebt);

        if (updatedCustomerDebt.getCustomerId() != null) existingCustomerDebt.setCustomer((Customer) entities.get("customer"));
        if(updatedCustomerDebt.getDate() != null) existingCustomerDebt.setDate(updatedCustomerDebt.getDate());

        if (updatedCustomerDebt.getTotalAmount() <= 0) throw new BusinessException("El importe total debe ser mayor a 0.");
        else existingCustomerDebt.setTotalAmount(updatedCustomerDebt.getTotalAmount());

        if (updatedCustomerDebt.getAmountPaid() < 0) throw new BusinessException("El importe pagado debe ser mayor o igual a 0.");
        else existingCustomerDebt.setAmountPaid(updatedCustomerDebt.getAmountPaid());

        return customerDebtRepository.save(existingCustomerDebt);
    }

    private Map<String, Object> fetchRelatedEntities(CustomerDebtRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getCustomerId()));

        Map<String, Object> entities = new HashMap<>();
        entities.put("customer", customer);
        return entities;
    }

    public void deleteCustomerDebt(Integer id) {
        if (!customerDebtRepository.existsById(id)) {
            throw new ResourceNotFoundException("Deuda de cliente", id);
        }
        customerDebtRepository.deleteById(id);
    }
}
