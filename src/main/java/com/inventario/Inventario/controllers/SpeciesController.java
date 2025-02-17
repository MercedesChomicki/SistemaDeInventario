package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.SpeciesRequestDTO;
import com.inventario.Inventario.entities.Species;
import com.inventario.Inventario.services.SpeciesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/species")
public class SpeciesController {

    @Autowired
    private SpeciesService speciesService;

    /**
     * Obtener todas las especies con opción de ordenación
     **/
    @GetMapping()
    public List<Species> getAllSpecies(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return speciesService.getAllSpeciesSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Species> getSpeciesById(@PathVariable Integer id) {
        return ResponseEntity.ok(speciesService.getSpeciesById(id));
    }

    @PostMapping
    public ResponseEntity<Species> createSpecies(@RequestBody SpeciesRequestDTO speciesRequestDTO) {
        Species newSpecies = speciesService.createSpecies(speciesRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSpecies);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Species> updateSpecies(@PathVariable Integer id, @RequestBody SpeciesRequestDTO updatedSpecies) {
        Species updated = speciesService.updateSpecies(id, updatedSpecies);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSpecies(@PathVariable Integer id) {
        speciesService.deleteSpecies(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }
}
