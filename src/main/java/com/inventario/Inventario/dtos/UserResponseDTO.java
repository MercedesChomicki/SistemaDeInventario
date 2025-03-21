package com.inventario.Inventario.dtos;

import com.inventario.Inventario.entities.UserRole;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private UserRole role;
    private String phone;
    private LocalDateTime registrationTime;
}
