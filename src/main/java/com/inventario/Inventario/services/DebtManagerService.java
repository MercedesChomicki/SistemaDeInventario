package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.*;
import com.inventario.Inventario.entities.*;
import com.inventario.Inventario.entities.Customer;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.mappers.DebtMapper;
import com.inventario.Inventario.repositories.CustomerRepository;
import com.inventario.Inventario.repositories.DebtDetailRepository;
import com.inventario.Inventario.repositories.DebtRepository;
import com.inventario.Inventario.repositories.ProductRepository;
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
    private final DebtPaymentService debtPaymentService;
    private final ProductRepository productRepository;

    public Debt getDebtById(Integer id) {
        return debtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La deuda", id));
    }

    public Debt getOrCreateDebt(Integer customerId) {
        Customer customer = getCustomer(customerId);
        // Buscar deudas existentes que no estén pagadas completamente
        Debt unpaidDebt = debtRepository.findByCustomerAndStatusIn(
                customer, List.of(DebtStatus.PENDING, DebtStatus.PARTIALLY_PAID));

        return unpaidDebt != null ? unpaidDebt : debtRepository.save(new Debt(customer, LocalDateTime.now()));
    }

    private Customer getCustomer(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", customerId));
    }

    public List<DebtDetail> processAndSaveDebtDetails(Debt debt, List<DetailResponseDTO> cartDetails) {
        // Convertimos la lista actual de detalles en un Map para búsqueda rápida por productId
        Map<Integer, DebtDetail> existingDetails = debt.getDetails().stream()
                .collect(Collectors.toMap(detail -> detail.getProduct().getId(), detail -> detail));

        // Obtener todos los productIds de la lista de cartDetails
        List<Integer> productIds = cartDetails.stream()
                .map(DetailResponseDTO::getProductId)
                .toList();

        // Obtener los productos en una sola consulta a la BD
        Map<Integer, Product> productsMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        List<DebtDetail> newDetails = new ArrayList<>();

        for (DetailResponseDTO detailDTO : cartDetails) {
            Integer productId = detailDTO.getProductId();
            Integer quantity = detailDTO.getQuantity();

            Product product = productsMap.get(productId);
            if(product == null)
                throw new ResourceNotFoundException("Producto", productId);

            existingDetails.computeIfPresent(productId, (id, detail) -> {
                detail.updateDetailValues(quantity);
                return detail;
            });

            existingDetails.computeIfAbsent(productId, id -> {
                DebtDetail newDetail = new DebtDetail(debt, product, quantity);
                newDetails.add(newDetail);
                return newDetail;
            });
        }
        debt.getDetails().addAll(newDetails);
        debtDetailRepository.saveAll(debt.getDetails());

        return debt.getDetails();
    }

    @Transactional
    public DebtResponseDTO saveDebt(Debt debt, List<? extends TransactionDetail> details, BigDecimal amount, PaymentMethod paymentMethod) {
        BigDecimal total = debtPaymentService.calculateTotalWithoutSurcharge(details);
        BigDecimal surcharge = BigDecimal.ZERO;
        if(paymentMethod == PaymentMethod.CARD || paymentMethod == PaymentMethod.TRANSFER){
            surcharge = debtPaymentService.calculateSurcharge(amount);
        }

        if (amount.compareTo(total) >= 0)
            throw new IllegalArgumentException("No se puede crear una deuda si está pagando el total");

        debt.recalculateDebt(total, amount, surcharge);
        debtRepository.save(debt);
        DebtResponseDTO dto = debtMapper.toDTO(debt);
        dto.setDetails(debt.getDetails().stream()
                .map(debtMapper::toDetailDTO)
                .toList());
        return dto;
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
                .map(TransactionDetail::getSubtotal) // Obtiene todos los subtotales
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Suma los subtotales en una sola operación

        debt.setAmountTotal(total);
        debt.setAmountDue(total.subtract(debt.getAmountPaid()));

        return debt;
    }

    public void validateDebtCreation(DebtRequestDTO dto, CartResponseDTO cart) {
        if (cart.getDetails() == null || cart.getDetails().isEmpty()) {
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