package com.myproyect.springboot.controller;


import com.myproyect.springboot.model.EstacionTrabajoDTO;
import com.myproyect.springboot.service.EstacionTrabajoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estaciones-trabajo")
public class EstacionTrabajoController {

    private final EstacionTrabajoService estacionTrabajoService;

    public EstacionTrabajoController(final EstacionTrabajoService estacionTrabajoService) {
        this.estacionTrabajoService = estacionTrabajoService;
    }

    @GetMapping
    public ResponseEntity<List<EstacionTrabajoDTO>> getAllEstacionesTrabajo() {
        return ResponseEntity.ok(estacionTrabajoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstacionTrabajoDTO> getEstacionTrabajo(@PathVariable final Long id) {
        return ResponseEntity.ok(estacionTrabajoService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createEstacionTrabajo(@RequestBody final EstacionTrabajoDTO estacionTrabajoDTO) {
        final Long createdId = estacionTrabajoService.create(estacionTrabajoDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEstacionTrabajo(@PathVariable final Long id,
                                                      @RequestBody final EstacionTrabajoDTO estacionTrabajoDTO) {
        estacionTrabajoService.update(id, estacionTrabajoDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstacionTrabajo(@PathVariable final Long id) {
        estacionTrabajoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
