package com.inventario.Inventario.security.services;

import com.inventario.Inventario.dtos.LoginRequestDTO;
import com.inventario.Inventario.dtos.UserRequestDTO;
import com.inventario.Inventario.dtos.UserResponseDTO;
import com.inventario.Inventario.entities.UserEntity;
import com.inventario.Inventario.mappers.UserEntityMapper;
import com.inventario.Inventario.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder pwdEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserEntityMapper userEntityMapper;

    public String login(LoginRequestDTO u) {
        UserEntity user = userRepository.findByEmail(u.getUsername());
        if (user == null) throw new UsernameNotFoundException("Usuario no encontrado");

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword());

        authManager.authenticate(token);
        return jwtService.generateToken(u.getUsername());
    }

    public UserResponseDTO registerUser(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario ya está registrado");

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(pwdEncoder.encode(dto.getPassword()));
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setPhone(dto.getPhone());
        user.setRegistrationTime(LocalDateTime.now());

        UserEntity savedUser = userRepository.save(user);
        return userEntityMapper.toDTO(savedUser);
    }
}
