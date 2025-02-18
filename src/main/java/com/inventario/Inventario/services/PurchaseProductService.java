package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.PurchaseProductRequestDTO;
import com.inventario.Inventario.dtos.PurchaseProductResponseDTO;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.entities.Purchase;
import com.inventario.Inventario.entities.PurchaseProduct;
import com.inventario.Inventario.entities.PurchaseProductId;
import com.inventario.Inventario.exceptions.BusinessException;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.ProductRepository;
import com.inventario.Inventario.repositories.PurchaseProductRepository;
import com.inventario.Inventario.repositories.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PurchaseProductService {

    private final PurchaseProductRepository purchaseProductRepository;
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;

    public List<PurchaseProductResponseDTO> getAllPurchaseProductsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);

        List<PurchaseProduct> purchaseProducts = purchaseProductRepository.findAll(sort);

        return purchaseProducts.stream()
                .map(pp -> new PurchaseProductResponseDTO(
                            pp.getPurchase().getId(),
                            pp.getProduct().getId(),
                            pp.getQuantity(),
                            pp.getUnitPrice()))
                .toList();
    }

    public PurchaseProductResponseDTO getPurchaseProductById(PurchaseProductId id) {
        PurchaseProduct pp = purchaseProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto en compra", id));

        Integer purchaseId = pp.getPurchase().getId();
        Integer productId = pp.getProduct().getId();

        return new PurchaseProductResponseDTO(purchaseId, productId, pp.getQuantity(), pp.getUnitPrice());
    }

    public PurchaseProduct createPurchaseProduct(PurchaseProductRequestDTO dto) {
        Map<String, Object> entities = fetchRelatedEntities(dto);

        PurchaseProductId purchaseProductId = new PurchaseProductId(dto.getPurchaseId(), dto.getProductId());

        PurchaseProduct purchaseProduct = new PurchaseProduct();
        purchaseProduct.setId(purchaseProductId);
        purchaseProduct.setPurchase((Purchase) entities.get("purchase"));
        purchaseProduct.setProduct((Product) entities.get("product"));
        purchaseProduct.setQuantity(dto.getQuantity());
        purchaseProduct.setUnitPrice(dto.getUnitPrice());

        return purchaseProductRepository.save(purchaseProduct);
    }

    public PurchaseProductResponseDTO updatePurchaseProduct(PurchaseProductId id, PurchaseProductRequestDTO updatedPurchaseProduct) {
        PurchaseProduct existingPurchaseProduct = purchaseProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto en compra", id));

        Map<String, Object> entities = fetchRelatedEntities(updatedPurchaseProduct);

        PurchaseProductId purchaseProductId = new PurchaseProductId(updatedPurchaseProduct.getPurchaseId(), updatedPurchaseProduct.getProductId());

        if (updatedPurchaseProduct.getPurchaseId() != null && updatedPurchaseProduct.getProductId() != null) existingPurchaseProduct.setId(purchaseProductId);
        if (updatedPurchaseProduct.getPurchaseId() != null) existingPurchaseProduct.setPurchase((Purchase) entities.get("purchase"));
        if (updatedPurchaseProduct.getProductId() != null) existingPurchaseProduct.setProduct((Product) entities.get("product"));
        if (updatedPurchaseProduct.getQuantity() <= 0) throw new BusinessException("La cantidad debe ser mayor a 0.");
        else existingPurchaseProduct.setQuantity(updatedPurchaseProduct.getQuantity());
        if (updatedPurchaseProduct.getUnitPrice() <= 0.0) throw new BusinessException("El precio unitario debe ser mayor a 0.");
        else existingPurchaseProduct.setUnitPrice(updatedPurchaseProduct.getUnitPrice());

        purchaseProductRepository.save(existingPurchaseProduct);
        return new PurchaseProductResponseDTO(
                existingPurchaseProduct.getPurchase().getId(),
                existingPurchaseProduct.getProduct().getId(),
                existingPurchaseProduct.getQuantity(),
                existingPurchaseProduct.getUnitPrice());
    }

    private Map<String, Object> fetchRelatedEntities(PurchaseProductRequestDTO dto) {
        Purchase purchase = purchaseRepository.findById(dto.getPurchaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Compra", dto.getPurchaseId()));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", dto.getProductId()));

        Map<String, Object> entities = new HashMap<>();
        entities.put("purchase", purchase);
        entities.put("product", product);
        return entities;
    }

    public void deletePurchaseProduct(PurchaseProductId id) {
        if (!purchaseProductRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto en compra", id);
        }
        purchaseProductRepository.deleteById(id);
    }
}