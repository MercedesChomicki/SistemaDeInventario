package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.UserRequestDTO;
import com.inventario.Inventario.entities.Admin;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.AdministratorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdministratorRepository administratorRepository;

    public List<Admin> getAllAdministratorsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return administratorRepository.findAll(sort);
    }

    public Admin getAdministratorById(Integer id) {
        return administratorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador", id));
    }

    public Admin createAdministrator(UserRequestDTO dto) {
        Admin admin = new Admin();
        admin.setFirstname(dto.getFirstname());
        admin.setLastname(dto.getLastname());
        admin.setEmail(dto.getEmail());
        admin.setPassword(dto.getPassword());
        admin.setPhone(dto.getPhone());

        return administratorRepository.save(admin);
    }

    public Admin updateAdministrator(Integer id, UserRequestDTO updatedAdministrator) {
        Admin existingAdmin = administratorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador",id));

        if(updatedAdministrator.getFirstname() != null) existingAdmin.setFirstname(updatedAdministrator.getFirstname());
        if(updatedAdministrator.getLastname() != null) existingAdmin.setLastname(updatedAdministrator.getLastname());
        if(updatedAdministrator.getEmail() != null) existingAdmin.setEmail(updatedAdministrator.getEmail());
        if(updatedAdministrator.getPassword() != null) existingAdmin.setPassword(updatedAdministrator.getPassword());
        if(updatedAdministrator.getPhone() != null) existingAdmin.setPhone(updatedAdministrator.getPhone());

        return administratorRepository.save(existingAdmin);
    }

    public void deleteAdministrator(Integer id) {
        if (!administratorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Administrador", id);
        }
        administratorRepository.deleteById(id);
    }

}
