package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.LoginRequest;
import com.inventario.Inventario.dtos.UserRequestDTO;
import com.inventario.Inventario.dtos.UserResponseDTO;
import com.inventario.Inventario.security.JwtService;
import com.inventario.Inventario.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest u) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword());
        try {
            Authentication authentication = authManager.authenticate(token);

            if(authentication.isAuthenticated()) {
                String jwtToken = jwtService.generateToken(u.getUsername());
                return new ResponseEntity<>(jwtToken, HttpStatus.OK);
            }

        } catch (BadCredentialsException e) {
            // logger
        }
        return new ResponseEntity<>("Invalid Credentials",HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRequestDTO dto) {
        UserResponseDTO newUser = userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}