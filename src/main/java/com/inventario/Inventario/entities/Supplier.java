package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    @Setter(AccessLevel.NONE)
    private Integer id;

    // FISCAL DATA
    @NonNull // Usado para el contructor con todos los atributos excepto el ID
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    @NonNull
    private String documentNumber;
    @NonNull
    private String businessName; // Razón social
    @NonNull
    private String sellerName;
    @NonNull
    @Enumerated(EnumType.STRING)
    private TaxCategory taxCategory;
    @NonNull
    @Enumerated(EnumType.STRING)
    private LegalEntity legalEntity;

    // ADDRESS
    @NonNull
    private String country;
    @NonNull
    private String province;
    @NonNull
    private String city;
    @NonNull
    private String address;
    @NonNull
    private String floorOrApartment;
    @NonNull
    private String postalCode;

    // CONTACT
    @NonNull
    private String phone;
    @NonNull
    private String email;
}