package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.LoginRequestDTO;
import com.inventario.Inventario.dtos.UserRequestDTO;
import com.inventario.Inventario.dtos.UserResponseDTO;
import com.inventario.Inventario.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO user) {
        return ResponseEntity.ok(authService.login(user));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRequestDTO dto) {
        UserResponseDTO newUser = authService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}