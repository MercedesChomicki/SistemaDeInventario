package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.PurchaseDetailRequestDTO;
import com.inventario.Inventario.dtos.PurchaseRequestDTO;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.entities.Purchase;
import com.inventario.Inventario.entities.PurchaseDetail;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseManagerService {

    private final ProductRepository productRepository;
    private final StockService stockService;

    public List<PurchaseDetail> processDetails(PurchaseRequestDTO dto, Purchase purchase){
        List<Product> updatedProducts = new ArrayList<>();

        List<PurchaseDetail> details = dto.getDetails().stream()
                .map(detail -> createPurchaseDetail(purchase, detail, updatedProducts))
                .collect(Collectors.toList());

        if (!updatedProducts.isEmpty())
            productRepository.saveAll(updatedProducts); // Batch save de productos actualizados

        return details;
    }

    public PurchaseDetail createPurchaseDetail(Purchase purchase, PurchaseDetailRequestDTO detail, List<Product> updatedProducts) {
        Product product = productRepository.findById(detail.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", detail.getProductId()));

        if(!product.getCost().equals(detail.getPrice())) {
            product.setCost(detail.getPrice());
            updatedProducts.add(product);
        }
        PurchaseDetail purchaseDetail = new PurchaseDetail(purchase, product, detail.getQuantity());
        stockService.increaseStock(product, detail.getQuantity());

        return purchaseDetail;
    }
}
