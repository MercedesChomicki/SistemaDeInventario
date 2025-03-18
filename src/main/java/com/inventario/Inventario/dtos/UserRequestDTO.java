package com.inventario.Inventario.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.inventario.Inventario.entities.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import java.util.Set;

@Getter
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
    @JsonDeserialize(contentAs = UserRole.class)
    private Set<UserRole> roles;
    private String phone;
}
