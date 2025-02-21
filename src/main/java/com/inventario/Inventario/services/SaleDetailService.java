package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.SaleDetailRequestDTO;
import com.inventario.Inventario.entities.Cart;
import com.inventario.Inventario.entities.SaleDetail;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.exceptions.BusinessException;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.SaleDetailRepository;
import com.inventario.Inventario.repositories.SaleRepository;
import com.inventario.Inventario.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SaleDetailService {

    private final SaleDetailRepository saleDetailRepository;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public List<SaleDetail> getAllSaleDetailsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return saleDetailRepository.findAll(sort);
    }

    public SaleDetail getSaleDetailById(Long id) {
        return saleDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", id));
    }

    public SaleDetail createSaleDetail(SaleDetailRequestDTO dto) {
        Map<String, Object> entities = fetchRelatedEntities(dto);

        SaleDetail saleDetail = new SaleDetail();

        return saleDetailRepository.save(saleDetail);
    }

    public SaleDetail updateSaleDetail(Long id, SaleDetailRequestDTO updatedSaleDetail) {
        SaleDetail existingSaleDetail  = saleDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", id));

        Map<String, Object> entities = fetchRelatedEntities(updatedSaleDetail);

        if (updatedSaleDetail.getProductId() != null) existingSaleDetail.setProduct((Product) entities.get("product"));

        if (updatedSaleDetail.getQuantity() <= 0) throw new BusinessException("La cantidad debe ser mayor a 0.");
        else existingSaleDetail.setQuantity(updatedSaleDetail.getQuantity());



        return saleDetailRepository.save(existingSaleDetail);
    }

    private Map<String, Object> fetchRelatedEntities(SaleDetailRequestDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", dto.getProductId()));

        Map<String, Object> entities = new HashMap<>();
        entities.put("product", product);
        return entities;
    }

    public void deleteSaleDetail(Long id) {
        if (!saleDetailRepository.existsById(id)) {
            throw new ResourceNotFoundException("Carrito", id);
        }
        saleDetailRepository.deleteById(id);
    }

    /** Como usuario quiero poder generar un reporte en cualquier momento
     para saber qué productos y qué cantidad de cada uno se va vendiendo en el día,
     con la posibilidad de ordenarlos de menor a mayor o de mayor a menor
     */
    public List<Object[]> getSalesReportByDate(LocalDate date, boolean asc) {
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(23, 59, 59);

        List<Object[]> results = saleDetailRepository.getSalesReportByDate(startDate, endDate);
        results.sort(Comparator.comparing(o -> (Integer) o[1])); // Ordena por cantidad vendida

        if (!asc) Collections.reverse(results); // Si quiere orden descendente, lo invierte

        return results;
    }
}
