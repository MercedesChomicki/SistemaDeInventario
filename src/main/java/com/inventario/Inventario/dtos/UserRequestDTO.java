package com.inventario.Inventario.dtos;

import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
public class UserRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String phone;

    private LocalDateTime registration_time;

    @PrePersist
    public void prePersist() {
        this.registration_time = ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).toLocalDateTime();
    }
}
