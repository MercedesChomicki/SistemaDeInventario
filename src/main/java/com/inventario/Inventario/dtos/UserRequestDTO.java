package com.inventario.Inventario.dtos;

import com.inventario.Inventario.entities.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    @NotBlank
    private String email;
    @NotBlank
    private UserRole role;
    private String phone;
}
