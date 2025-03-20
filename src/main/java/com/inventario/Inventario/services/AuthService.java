package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.LoginRequestDTO;
import com.inventario.Inventario.dtos.UserRequestDTO;
import com.inventario.Inventario.dtos.UserResponseDTO;
import com.inventario.Inventario.entities.UserEntity;
import com.inventario.Inventario.entities.UserRole;
import com.inventario.Inventario.entities.UserRoleEntity;
import com.inventario.Inventario.mappers.UserMapper;
import com.inventario.Inventario.repositories.UserRepository;
import com.inventario.Inventario.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder pwdEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserMapper userMapper;

    public String login(LoginRequestDTO u) {
        UserEntity user = userRepository.findByEmail(u.getUsername());

        if (user == null)
            throw new UsernameNotFoundException("Usuario no encontrado");

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword());

        authManager.authenticate(token);

        return jwtService.generateToken(u.getUsername());
    }

    /*public String register(UserRequestDTO request) {
        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(pwdEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return jwtService.generateToken(user);
    }*/

    public UserResponseDTO registerUser(UserRequestDTO dto) {
        Set<UserRoleEntity> roles = new HashSet<>();
        for(UserRole role : dto.getRoles()) {
            UserRoleEntity userRole = new UserRoleEntity();
            userRole.setName(role);
            roles.add(userRole);
        }
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(pwdEncoder.encode(dto.getPassword()));
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());
        user.setRoles(roles);
        user.setPhone(dto.getPhone());
        user.setRegistrationTime(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }
}
