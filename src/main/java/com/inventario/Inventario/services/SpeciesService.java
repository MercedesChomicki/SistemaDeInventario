package com.inventario.Inventario.services;

import com.inventario.Inventario.entities.Species;
import com.inventario.Inventario.repositories.SpeciesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpeciesService {

    @Autowired
    private SpeciesRepository speciesRepository;

    public List<Species> getAllSpeciesSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return speciesRepository.findAll(sort);
    }

    public Optional<Species> getSpeciesById(Integer id) {
        return speciesRepository.findById(id);
    }

    public Species saveSpecies(Species species) {
        return speciesRepository.save(species);
    }

    public Optional<Species> updateSpecies(Integer id, Species updatedSpecies) {
        return speciesRepository.findById(id).map(existingSpecies -> {
            existingSpecies.setName(updatedSpecies.getName());
            return speciesRepository.save(existingSpecies);
        });
    }

    public boolean deleteSpecies(Integer id) {
        if (speciesRepository.existsById(id)) {
            speciesRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
