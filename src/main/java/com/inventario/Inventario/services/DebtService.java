package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.*;
import com.inventario.Inventario.entities.*;

import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.mappers.DebtMapper;
import com.inventario.Inventario.repositories.DebtDetailRepository;
import com.inventario.Inventario.repositories.DebtRepository;
import com.inventario.Inventario.repositories.CustomerRepository;
import com.inventario.Inventario.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.inventario.Inventario.services.SaleService.SURCHARGE_PERCENTAGE;

@Service
@RequiredArgsConstructor
public class DebtService {

    private final DebtRepository debtRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final DebtDetailRepository debtDetailRepository;
    private final DebtMapper debtMapper;

    public List<DebtResponseDTO> getAllDebtsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return debtRepository.findAll(sort)
                .stream()
                .map(debtMapper::toDTO)
                .collect(Collectors.toList());
    }

    public DebtResponseDTO getDebtById(Integer id) {
        Debt debt = debtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La deuda", id));
        return debtMapper.toDTO(debt);
    }

    @Transactional
    public DebtResponseDTO createDebt(DebtRequestDTO dto) {
        Customer customer = getCustomer(dto.getCustomerId());
        Debt debt = getOrCreateDebt(customer, dto.getDate());

        List<DebtDetail> details = processDebtDetails(dto, debt);
        debtDetailRepository.saveAll(details);

        updateDebtValues(debt, details, dto.getPayInCash(), dto.getPayWithTransfer());

        // Guardamos nuevamente para actualizar los montos y detalles
        debtRepository.save(debt);
        return debtMapper.toDTO(debt);
    }

    private Customer getCustomer(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", customerId));
    }

    private Debt getOrCreateDebt(Customer customer, LocalDateTime date) {
        // Buscar deudas existentes que no estén pagadas completaamente
        Debt unpaidDebt = debtRepository.findByCustomerAndStatusIn(
                customer, List.of(DebtStatus.PENDING, DebtStatus.PARTIALLY_PAID));

        return unpaidDebt != null ? unpaidDebt : debtRepository.save(new Debt(customer, date));
    }

    // Procesa los detalles de la deuda y valida stock
    private List<DebtDetail> processDebtDetails(DebtRequestDTO dto, Debt debt) {
        if (dto.getDetails() == null || dto.getDetails().isEmpty()) {
            throw new IllegalArgumentException("No se puede crear una deuda sin productos");
        }

        // Obtener todos los IDs de productos en una sola consulta
        Set<Integer> productIds = dto.getDetails().stream()
                .map(DebtDetailRequestDTO::getProductId)
                .collect(Collectors.toSet());

        // Traer todos los productos en una sola consulta
        List<Product> products = productRepository.findAllById(productIds);
        Map<Integer, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // Validar que todos los productos existan
        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("Algunos productos no se encuentran en el sistema.");
        }

        // Convertimos la lista actual de detalles en un Map para búsqueda rápida por productId
        Map<Integer, DebtDetail> existingDetails = debt.getDetails().stream()
                .collect(Collectors.toMap(detail -> detail.getProduct().getId(), detail -> detail));

        List<DebtDetail> newDetails = new ArrayList<>();

        for (DebtDetailRequestDTO detailDTO : dto.getDetails()) {
            Integer productId = detailDTO.getProductId();
            Integer quantity = detailDTO.getQuantity();

            Product product = productMap.get(productId);
            if (product.getStock() < quantity) {
                throw new IllegalArgumentException("Stock insuficiente para " + product.getName());
            }

            // Descontar stock en memoria (Hibernate se encargará de actualizarlo en la BD)
            product.setStock(product.getStock() - quantity);

            // Si el detalle ya existe, actualizarlo
            if (existingDetails.containsKey(productId)) {
                existingDetails.get(productId).updateDetailValues(quantity);
            } else {
                // Si el producto no está en la deuda, agregar un nuevo detalle
                DebtDetail newDetail = new DebtDetail(debt, product, quantity);
                newDetails.add(newDetail);
            }
        }
        // Agregar los nuevos detalles a la deuda
        debt.getDetails().addAll(newDetails);

        return debt.getDetails();
    }

    private void updateDebtValues(Debt debt, List<DebtDetail> details, BigDecimal payInCash, BigDecimal payWithTransfer) {
        BigDecimal total = details.stream()
                .map(DebtDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPayment = payInCash.add(payWithTransfer);

        if (payWithTransfer.compareTo(BigDecimal.ZERO) > 0) {
            total = total.add(calculateSurcharge(payWithTransfer));
        }

        if (totalPayment.compareTo(total) > 0) {
            throw new IllegalArgumentException("No se puede crear una deuda si está pagando el total");
        }

        debt.updateDebtValues(total, totalPayment);
    }

    @Transactional
    public DebtResponseDTO payDebt(Integer id, boolean incash, BigDecimal amount) {
        Debt debt = debtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda de cliente", id));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a pagar debe ser mayor que cero.");
        }
        // Si el pago es con transferencia, volver al importe base
        BigDecimal baseAmount = !incash ? calculateBaseAmount(amount) : amount;
        System.out.println("BASE AMOUNT: "+baseAmount);
        if (baseAmount.compareTo(debt.getAmountDue()) > 0) {
            throw new IllegalArgumentException("El monto a pagar no puede ser mayor a la deuda pendiente.");
        }

        // Si el pago es por transferencia, calcular recargo
        BigDecimal surcharge = calculateSurcharge(baseAmount);
        debt.setAmountTotal(debt.getAmountTotal().add(surcharge));
        debt.setAmountPaid(debt.getAmountPaid().add(amount));
        debt.setAmountDue(debt.getAmountTotal().subtract(debt.getAmountPaid()));

        // Determinar el nuevo estado de la deuda
        boolean isCompleted = debt.getAmountDue().compareTo(BigDecimal.ZERO) == 0;
        debt.setStatus(isCompleted ? DebtStatus.PAID : DebtStatus.PARTIALLY_PAID);
        debt = debtRepository.save(debt);

        return isCompleted ? null : debtMapper.toDTO(debt);
        // TODO Agregar el monto pagado (baseAmount) al atributo debtsPaid de la tabla reportes cuando esté
    }
    /**
     * TODO este método va en reportes
        public BigDecimal paidToday(BigDecimal baseAmount){
            paidToday = getPaidToday().add(baseAmount);
        }
     */

    private BigDecimal calculateBaseAmount(BigDecimal amount) {
        BigDecimal surchargeRate = BigDecimal.ONE.add(
                SURCHARGE_PERCENTAGE.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)); // 1 + 0,07 = 1,07
        System.out.println("SURCHARGE RATE: "+surchargeRate);
        return amount.divide(surchargeRate, RoundingMode.HALF_UP); // 5029 / 1,07 = 4700
    }

    // Método para calcular el recargo por transferencia
    private BigDecimal calculateSurcharge(BigDecimal total) {
        return total
                .multiply(SURCHARGE_PERCENTAGE)
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

    // TODO al pagarse completamente una deuda, cambia su estado a PAID pero no se elimina
    public void deleteDebt(Integer id) {
        if (!debtRepository.existsById(id)) {
            throw new ResourceNotFoundException("Deuda", id);
        }
        debtRepository.deleteById(id);
    }
}
