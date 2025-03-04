package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.UserRequestDTO;
import com.inventario.Inventario.entities.Administrator;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.AdministratorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministratorService {

    private final AdministratorRepository administratorRepository;

    public List<Administrator> getAllAdministratorsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return administratorRepository.findAll(sort);
    }

    public Administrator getAdministratorById(Integer id) {
        return administratorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador", id));
    }

    public Administrator createAdministrator(UserRequestDTO dto) {
        Administrator admin = new Administrator();
        admin.setFirstname(dto.getFirstname());
        admin.setLastname(dto.getLastname());
        admin.setEmail(dto.getEmail());
        admin.setPassword(dto.getPassword());
        admin.setPhone(dto.getPhone());

        return administratorRepository.save(admin);
    }

    public Administrator updateAdministrator(Integer id, UserRequestDTO updatedAdministrator) {
        Administrator existingAdministrator = administratorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador",id));

        if(updatedAdministrator.getFirstname() != null) existingAdministrator.setFirstname(updatedAdministrator.getFirstname());
        if(updatedAdministrator.getLastname() != null) existingAdministrator.setLastname(updatedAdministrator.getLastname());
        if(updatedAdministrator.getEmail() != null) existingAdministrator.setEmail(updatedAdministrator.getEmail());
        if(updatedAdministrator.getPassword() != null) existingAdministrator.setPassword(updatedAdministrator.getPassword());
        if(updatedAdministrator.getPhone() != null) existingAdministrator.setPhone(updatedAdministrator.getPhone());

        return administratorRepository.save(existingAdministrator);
    }

    public void deleteAdministrator(Integer id) {
        if (!administratorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Administrador", id);
        }
        administratorRepository.deleteById(id);
    }

}
