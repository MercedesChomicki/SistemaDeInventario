package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.*;
import com.inventario.Inventario.entities.*;

import com.inventario.Inventario.mappers.DebtMapper;
import com.inventario.Inventario.repositories.DebtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DebtService {

    private final DebtRepository debtRepository;
    private final DebtMapper debtMapper;
    private final DebtManagerService debtManagerService;
    private final DebtPaymentService debtPaymentService;
    private final CartService cartService;

    public List<DebtResponseDTO> getAllDebtsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return debtRepository.findAll(sort)
                .stream()
                .map(debt -> {
                    DebtResponseDTO dto = debtMapper.toDTO(debt);
                    dto.setDetails(debt.getDetails().stream()
                            .map(debtMapper::toDetailDTO).toList());
                    return dto;
                }).toList();
    }

    public DebtResponseDTO getDebtById(Integer id) {
        Debt debt = debtManagerService.getDebtById(id);
        return debtMapper.toDTO(debt);
    }

    @Transactional
    public DebtResponseDTO createDebt(DebtRequestDTO dto) {
        CartResponseDTO cart = cartService.getCartByUserId(dto.getUserId());
        debtManagerService.validateDebtCreation(dto, cart);
        Debt debt = debtManagerService.getOrCreateDebt(dto.getCustomerId());
        List<DebtDetail> details = debtManagerService.processAndSaveDebtDetails(debt, cart.getDetails());
        DebtResponseDTO debtDTO = debtManagerService.saveDebt(debt, details, dto.getPayment().getAmount(), dto.getPayment().getPaymentMethod());
        cartService.clearCart(dto.getUserId());
        return debtDTO;
    }

    @Transactional
    public DebtResponseDTO processDebtPayment(Integer id, BigDecimal amount, PaymentMethod paymentMethod) {
        Debt debt = debtManagerService.getDebtById(id);
        debtPaymentService.processDebtPayment(debt, amount, paymentMethod);
        return debtManagerService.finalizeDebtPayment(debt);
    }

    public List<DebtResponseDTO> getAllUnpaidDebts(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return debtRepository.findByStatusNot(DebtStatus.PAID, sort)
                .stream()
                .map(debtMapper::toDTO)
                .collect(Collectors.toList());
    }

    /*@Transactional
    public DebtResponseDTO updateDebtValues(Integer debtId){
        Debt debt = debtManagerService.updateDebtDetails(debtId);
        debtRepository.save(debt);
        return debtMapper.toDTO(debt);
    }*/
}
