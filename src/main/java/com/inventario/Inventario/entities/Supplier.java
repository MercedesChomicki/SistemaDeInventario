package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.*;

/*
     + DATOS FISCALES
        1. Tipo de documento:
          - DNI
          - CUIL/CUIT

        2. Número (de DNI, CUIT o CUIL)

        3. Razón social (Nombre de la Empresa - Opcional)

        4. Nombre del vendedor

        5. Categoría impositiva:
          - Responsable inscripto
          - Consumidor final
          - Monotributista
          - Exento
          - IVA no alcanzado

        6. Personería:
          - Fisica
          - Jurídica (Empresa)

     + DOMICILIO
        1. País
        2. Provincia
        3. Ciudad/Localidad
        4. Domicilio
        5. Piso/Depto
        6. Código Postal

     + CONTACTO
        1. Teléfono
        2. Email
*/

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    @Setter(AccessLevel.NONE) // Evita setter solo para el id
    private Integer id;

    @Column(nullable = false, length = 100)
    private String firstname;

    @Column(nullable = false, length = 45)
    private String lastname;

    @Column(name = "phone", nullable = false, length = 45)
    private String phone;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String company;

}