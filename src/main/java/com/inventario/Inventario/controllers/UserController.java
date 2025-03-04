package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.UserRequestDTO;
import com.inventario.Inventario.entities.User;
import com.inventario.Inventario.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserRequestDTO dto) {
        User newUser = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}
