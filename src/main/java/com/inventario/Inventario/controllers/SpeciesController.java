package com.inventario.Inventario.controllers;

import com.inventario.Inventario.entities.Species;
import com.inventario.Inventario.exceptions.SpeciesNotFoundException;
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
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return speciesService.getAllSpeciesSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Species> getSpeciesById(@PathVariable Integer id) {
        return ResponseEntity.ok(speciesService.getSpeciesById(id)
                .orElseThrow(() -> new SpeciesNotFoundException(id)));
    }

    @PostMapping
    public ResponseEntity<Species> saveSpecies(@RequestBody Species species) {
        Species newSpecies = speciesService.saveSpecies(species);
        return ResponseEntity.ok(newSpecies);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Species> updateSpecies(@PathVariable Integer id, @RequestBody Species updatedSpecies) {
        return speciesService.updateSpecies(id, updatedSpecies)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSpecies(@PathVariable Integer id) {
        if (speciesService.deleteSpecies(id)) {
            return ResponseEntity.ok("Se ha eliminado exitosamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La especie no se ha podido eliminar.");
        }
    }
}
