package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.ProductRequestDTO;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.exceptions.BusinessException;
import com.inventario.Inventario.exceptions.NotFoundException;
import com.inventario.Inventario.exceptions.ProductNotFoundException;
import com.inventario.Inventario.services.CloudinaryService;
import com.inventario.Inventario.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /*private final CloudinaryService cloudinaryService;

    @Autowired
    public ProductController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        System.out.println("INGRESÓ AQUÍ: ProductController --> POST /upload");
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El archivo está vacío");
            }
            // Simulación de guardado (cámbialo según tu lógica)
            String fileName = file.getOriginalFilename();
            System.out.println("Archivo recibido: " + fileName);

            String imageUrl = cloudinaryService.uploadImage(UUID.randomUUID(), file);
            System.out.println("ImageUrl: " + imageUrl);

            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir la imagen");
        }
    }
*/
    /**
     * Obtener todos los productos con opción de ordenación
     **/
    @GetMapping()
    public List<Product> getAllProducts(
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return productService.getAllProductsSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id)));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        Product newProduct = productService.createProduct(productRequestDTO);
        return ResponseEntity.ok(newProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody ProductRequestDTO updatedProduct) {
        return productService.updateProduct(id, updatedProduct)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
        try {
            if (productService.deleteProduct(id)) {
                return ResponseEntity.ok("Se ha eliminado exitosamente.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El producto no se ha podido eliminar.");
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el producto: " + e.getMessage());
        }
    }

    /** Como usuario quiero poder generar un reporte en cualquier momento
     para saber qué productos y qué cantidad de cada uno se va vendiendo en el día,
     con la posibilidad de ordenarlos de menor a mayor o de mayor a menor
     */
    @GetMapping("/sales-report")
    public ResponseEntity<List<Map<String, Object>>> getDailySalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "false") boolean asc
    ) {
        List<Object[]> report = productService.getSalesReportByDate(date, asc);

        // Convertir la lista de Object[] a lista de Map<String, Object> para que sea más clara en JSON
        List<Map<String, Object>> response = report.stream().map(obj -> {
            Map<String, Object> item = new HashMap<>();
            item.put("product", obj[0]);
            item.put("quantity", ((Number) obj[1]).longValue());
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}

