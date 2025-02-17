package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.SpeciesRequestDTO;
import com.inventario.Inventario.entities.Species;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpeciesService {

    private final SpeciesRepository speciesRepository;

    public List<Species> getAllSpeciesSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return speciesRepository.findAll(sort);
    }

    public Species getSpeciesById(Integer id) {
        return speciesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especie", id));
    }

    public Species createSpecies(SpeciesRequestDTO dto) {
        Species species = new Species();
        species.setName(dto.getName());
        return speciesRepository.save(species);
    }

    public Species updateSpecies(Integer id, SpeciesRequestDTO updatedSpecies) {
        Species existingSpecies = speciesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especie", id));

        if (updatedSpecies.getName() != null) existingSpecies.setName(updatedSpecies.getName());

        return speciesRepository.save(existingSpecies);
    }

    public void deleteSpecies(Integer id) {
        if (!speciesRepository.existsById(id)) {
            throw new ResourceNotFoundException("Especie", id);
        }
        speciesRepository.deleteById(id);
    }
}
