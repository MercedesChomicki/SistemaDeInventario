package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @Setter(AccessLevel.NONE)
    private Integer id;

    @NotBlank
    @Size(max = 50)
    private String username;

    @NotBlank
    private String password;

    @Column(nullable = false, length = 100)
    private String firstname;

    @Column(nullable = false, length = 100)
    private String lastname;

    @Email
    @NotBlank
    @Size(max = 80)
    private String email;

    /** ManyToMany (
     * fetch = FetchType.EAGER Para que cuando consulte el usuario traiga TODOS los roles asociados.
     * targetEntity = UserRoleEntity.class Es con la entidad con la que se va a establecer la relación.
     * cascade = CascadeType.PERSIST Guarda en la db las entidades relacionadas en caso de que aún no existan.
     )
     JoinTable (
     * name = "user_roles" Nombre de la tabla intermedia.
     * joinColumns = @JoinColumn(name = "user_id") Configura la clave foránea en UserEntity
     * inverseJoinColumns = @JoinColumn(name = "role_id") Configura la clave foránea de UserRoleEntity
     )
     Set: no permite tener elementos duplicados
     */
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = UserRoleEntity.class, cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRoleEntity> roles;

    @Column(length = 45)
    private String phone;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registrationTime;

    @PrePersist
    public void prePersist() {
        this.registrationTime = ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).toLocalDateTime();
    }
}