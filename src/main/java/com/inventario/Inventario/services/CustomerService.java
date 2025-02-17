package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.CustomerRequestDTO;
import com.inventario.Inventario.entities.Customer;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<Customer> getAllCustomersSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return customerRepository.findAll(sort);
    }

    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }

    public Customer createCustomer(CustomerRequestDTO dto) {
        Customer customer = new Customer();
        customer.setFirstname(dto.getFirstname());
        customer.setLastname(dto.getLastname());
        customer.setPhone(dto.getPhone());
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Integer id, CustomerRequestDTO updatedCustomer) {
        Customer existingCustomer  = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));

        if(updatedCustomer.getFirstname() != null) existingCustomer.setFirstname(updatedCustomer.getFirstname());
        if(updatedCustomer.getLastname() != null) existingCustomer.setLastname(updatedCustomer.getLastname());
        if(updatedCustomer.getPhone() != null) existingCustomer.setPhone(updatedCustomer.getPhone());

        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(Integer id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente", id);
        }
        customerRepository.deleteById(id);
    }
}
