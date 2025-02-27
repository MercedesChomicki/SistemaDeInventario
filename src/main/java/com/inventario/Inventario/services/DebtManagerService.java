package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.DebtDetailRequestDTO;
import com.inventario.Inventario.dtos.DebtRequestDTO;
import com.inventario.Inventario.dtos.DebtResponseDTO;
import com.inventario.Inventario.entities.*;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.mappers.DebtMapper;
import com.inventario.Inventario.repositories.CustomerRepository;
import com.inventario.Inventario.repositories.DebtDetailRepository;
import com.inventario.Inventario.repositories.DebtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DebtManagerService {

    private final DebtRepository debtRepository;
    private final DebtDetailRepository debtDetailRepository;
    private final CustomerRepository customerRepository;
    private final DebtMapper debtMapper;
    private final PaymentService paymentService;

    public Debt getDebtById(Integer id) {
        return debtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La deuda", id));
    }

    public Debt getOrCreateDebt(Integer customerId, LocalDateTime date) {
        Customer customer = getCustomer(customerId);
        // Buscar deudas existentes que no estén pagadas completamente
        Debt unpaidDebt = debtRepository.findByCustomerAndStatusIn(
                customer, List.of(DebtStatus.PENDING, DebtStatus.PARTIALLY_PAID));

        return unpaidDebt != null ? unpaidDebt : debtRepository.save(new Debt(customer, date));
    }

    private Customer getCustomer(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", customerId));
    }

    // Procesa los detalles de la deuda
    public List<DebtDetail> processAndSaveDebtDetails(DebtRequestDTO dto, Debt debt, Map<Integer, Product> productsMap) {
        // Convertimos la lista actual de detalles en un Map para búsqueda rápida por productId
        Map<Integer, DebtDetail> existingDetails = debt.getDetails().stream()
                .collect(Collectors.toMap(detail -> detail.getProduct().getId(), detail -> detail));

        List<DebtDetail> newDetails = new ArrayList<>();

        for (DebtDetailRequestDTO detailDTO : dto.getDetails()) {
            Integer productId = detailDTO.getProductId();
            Integer quantity = detailDTO.getQuantity();
            Product product = productsMap.get(productId);

            if (existingDetails.containsKey(productId)) {
                // Si el detalle ya existe, actualizarlo
                existingDetails.get(productId).updateDetailValues(quantity);
            } else {
                DebtDetail newDetail = new DebtDetail(debt, product, quantity);
                newDetails.add(newDetail);
            }
        }
        // Agregar los nuevos detalles a la deuda
        debt.getDetails().addAll(newDetails);
        debtDetailRepository.saveAll(debt.getDetails());
        return debt.getDetails();
    }

    @Transactional
    public DebtResponseDTO updateAndSaveDebt(Debt debt, List<DebtDetail> details, BigDecimal amount, boolean isInCash) {
        BigDecimal total = paymentService.calculateTotal(details);
        BigDecimal surcharge = BigDecimal.ZERO;
        if(!isInCash){
            surcharge = paymentService.calculateSurcharge(amount);
        }

        if (amount.compareTo(total) >= 0)
            throw new IllegalArgumentException("No se puede crear una deuda si está pagando el total");

        System.out.println("Recalculating surcharge: " + surcharge);
        debt.recalculateDebt(total, amount, surcharge);
        System.out.println("Updated surcharge in Debt: " + debt.getSurcharge());
        debtRepository.save(debt);
        return debtMapper.toDTO(debt);
    }

    public Debt updateDebtDetails(Integer debtId) {
        Debt debt = getDebtById(debtId);
        BigDecimal total = debt.getDetails().stream()
                .peek(detail -> {
                    Product product = detail.getProduct();
                    BigDecimal actualPrice = product.getCashPrice();
                    detail.setUnitPrice(actualPrice);
                    detail.setSubtotal(actualPrice.multiply(BigDecimal.valueOf(detail.getQuantity())));
                })
                .map(DebtDetail::getSubtotal) // Obtiene todos los subtotales
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Suma los subtotales en una sola operación

        debt.setAmountTotal(total);
        debt.setAmountDue(total.subtract(debt.getAmountPaid()));

        return debt;
    }

    public void validateDebtCreation(DebtRequestDTO dto) {
        if (dto.getDetails() == null || dto.getDetails().isEmpty()) {
            throw new IllegalArgumentException("No se puede crear una deuda sin productos.");
        }
        if (!customerRepository.existsById(dto.getCustomerId())) {
            throw new ResourceNotFoundException("Cliente", dto.getCustomerId());
        }
    }

    public DebtResponseDTO finalizeDebtPayment(Debt debt) {
        debtRepository.save(debt);
        return debt.getStatus() == DebtStatus.PAID ? null : debtMapper.toDTO(debt);
    }

}