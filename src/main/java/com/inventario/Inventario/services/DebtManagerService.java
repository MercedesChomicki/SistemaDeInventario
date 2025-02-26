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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.inventario.Inventario.constants.Constant.SURCHARGE_PERCENTAGE;

@Service
@RequiredArgsConstructor
public class DebtManagerService {

    private final DebtRepository debtRepository;
    private final DebtDetailRepository debtDetailRepository;
    private final CustomerRepository customerRepository;
    private final DebtMapper debtMapper;

    public Debt getDebtById(Integer id) {
        return debtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La deuda", id));
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
    public DebtResponseDTO updateAndSaveDebt(Debt debt, List<DebtDetail> details, BigDecimal payInCash, BigDecimal payWithTransfer) {
        BigDecimal total = calculateTotal(details, payWithTransfer);
        System.out.println("TOTAL: "+total);

        BigDecimal totalPayment = payInCash.add(payWithTransfer);

        if (totalPayment.compareTo(total) > 0)
            throw new IllegalArgumentException("No se puede crear una deuda si está pagando el total");

        debt.updateDebtValues(total, totalPayment);
        debtRepository.save(debt);
        return debtMapper.toDTO(debt);
    }

    public Debt getOrCreateDebt(Integer customerId, LocalDateTime date) {
        Customer customer = getCustomer(customerId);
        // Buscar deudas existentes que no estén pagadas completaamente
        Debt unpaidDebt = debtRepository.findByCustomerAndStatusIn(
                customer, List.of(DebtStatus.PENDING, DebtStatus.PARTIALLY_PAID));

        return unpaidDebt != null ? unpaidDebt : debtRepository.save(new Debt(customer, date));
    }

    private Customer getCustomer(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", customerId));
    }

    private BigDecimal calculateTotal(List<DebtDetail> details, BigDecimal payWithTransfer){
        BigDecimal total = details.stream()
                .map(DebtDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return payWithTransfer.compareTo(BigDecimal.ZERO) > 0
                ? total.add(calculateSurcharge(payWithTransfer))
                : total;
    }

    // Método para calcular el recargo por transferencia
    public BigDecimal calculateSurcharge(BigDecimal total) {
        return total.multiply(SURCHARGE_PERCENTAGE)
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

}