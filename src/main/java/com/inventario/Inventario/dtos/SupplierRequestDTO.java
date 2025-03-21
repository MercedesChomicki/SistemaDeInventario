package com.inventario.Inventario.dtos;

import com.inventario.Inventario.entities.DocumentType;
import com.inventario.Inventario.entities.LegalEntity;
import com.inventario.Inventario.entities.TaxCategory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class SupplierRequestDTO {

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    private String documentNumber;
    private String businessName; // Razón social
    private String sellerName;

    @Enumerated(EnumType.STRING)
    private TaxCategory taxCategory;

    @Enumerated(EnumType.STRING)
    private LegalEntity legalEntity;

    private String country;
    private String province;
    private String city;
    private String address;
    private String floorOrApartment;
    private String postalCode;

    private String phone;
    private String email;
}
