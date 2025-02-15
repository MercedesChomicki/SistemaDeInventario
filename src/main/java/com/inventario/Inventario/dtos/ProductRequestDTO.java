package com.inventario.Inventario.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductRequestDTO {

    @Schema(example = "PIP-001")
    @NotBlank
    private String code;

    @Schema(example = "Frontline gato común")
    @NotBlank
    private String name;

    private String description;

    @Schema(example = "6000")
    @NotNull
    @Positive
    private BigDecimal price;

    @Schema(example = "10")
    @NotNull
    @Min(0)
    private int stock;

    @Schema(example = "2026-02-15")
    private LocalDate expirationDate;

    @Schema(example = "null")
    private String image;

    @Schema(example = "1")
    @NotNull
    private Integer speciesId;

    @Schema(example = "1")
    @NotNull
    private Integer categoryId;

    @Schema(example = "1")
    @NotNull
    private Integer supplierId;

    // Getters y setters

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(Integer speciesId) {
        this.speciesId = speciesId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }
}
