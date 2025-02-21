package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.DebtRequestDTO;
import com.inventario.Inventario.dtos.DebtResponseDTO;
import com.inventario.Inventario.entities.Customer;
import com.inventario.Inventario.entities.Debt;

import com.inventario.Inventario.entities.Sale;
import com.inventario.Inventario.entities.SaleStatus;
import com.inventario.Inventario.exceptions.DebtPaidException;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.DebtRepository;
import com.inventario.Inventario.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.inventario.Inventario.services.SaleService.CARD_SURCHARGE_PERCENTAGE;

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

    public Debt payDebt(Integer id, boolean incash, BigDecimal amount) {
        Debt debt = debtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda de cliente", id));

        // Aplicar recargo si no se paga en efectivo
        applySurchargeIfNeeded(debt, incash);

        if(amount.compareTo(debt.getAmountDue()) > 0){
            throw new IllegalArgumentException("El monto ingresado supera a la deuda.");
        }

        if(amount.compareTo(debt.getAmountDue()) == 0) {
            // Deuda saldada
            markSalesAsPaid((ArrayList<Sale>) debt.getSales());
            deleteDebt(id);
            throw new DebtPaidException("La deuda ha sido completamente pagada y eliminada.");
        }

        // Pago parcial
        processPartialPayment(debt, amount);

        // TODO Agregar el monto pagado (amount) al atributo debtsPaid de la tabla reportes cuando esté

        return debtRepository.save(debt);
    }

    public Debt payDebtWithSurcharge(Integer id, boolean incash, BigDecimal amount) {
        Debt debt = debtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda de cliente", id));

        // Actualizar los precios de los productos

        return debtRepository.save(debt);
    }

    // Método para aplicar recargo si no se paga en efectivo
    private void applySurchargeIfNeeded(Debt debt, boolean incash) {
        if (!incash) {
            BigDecimal surcharge = debt.getAmountDue()
                    .multiply(CARD_SURCHARGE_PERCENTAGE)
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            debt.setAmountDue(debt.getAmountDue().add(surcharge));
            debt.setAmountTotal(debt.getAmountTotal().add(surcharge));
        }
    }

    // Método para marcar las ventas asociadas como pagadas
    private void markSalesAsPaid(ArrayList<Sale> sales) {
        for (Sale sale : sales) {
            sale.setStatus(SaleStatus.PAID);
        }
    }

    // Método para procesar un pago parcial
    private void processPartialPayment(Debt debt, BigDecimal amount) {
        debt.setAmountPaid(debt.getAmountPaid().add(amount));
        debt.setAmountDue(debt.getAmountDue().subtract(amount));

        BigDecimal remainingAmount = amount;
        for (Sale sale : debt.getSales()) {
            BigDecimal amountDebt = sale.getTotal().subtract(sale.getPayInCash());
            if (remainingAmount.compareTo(amountDebt) >= 0) {
                sale.setStatus(SaleStatus.PAID);
                remainingAmount = remainingAmount.subtract(amountDebt);
            } else {
                break; // Si no se cubre la deuda total de una venta, se detiene
            }
        }
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
