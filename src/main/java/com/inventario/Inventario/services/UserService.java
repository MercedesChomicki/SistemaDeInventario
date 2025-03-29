package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.UserRequestDTO;
import com.inventario.Inventario.dtos.UserResponseDTO;
import com.inventario.Inventario.entities.UserEntity;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.mappers.UserEntityMapper;
import com.inventario.Inventario.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userEntityMapper::toDTO)
                .toList();
    }

    public UserResponseDTO getUserById(Integer id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userEntityMapper.toDTO(user);
    }

    public UserEntity updateUser(Integer id, UserRequestDTO dto) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserEntityistrador",id));

        if(dto.getUsername() != null) existingUser.setUsername(dto.getUsername());
        if(dto.getPassword() != null) existingUser.setPassword(dto.getPassword());
        if(dto.getFirstname() != null) existingUser.setFirstname(dto.getFirstname());
        if(dto.getLastname() != null) existingUser.setLastname(dto.getLastname());
        if(dto.getEmail() != null) existingUser.setEmail(dto.getEmail());
        if(dto.getPhone() != null) existingUser.setPhone(dto.getPhone());

        return userRepository.save(existingUser);
    }

    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }
}
