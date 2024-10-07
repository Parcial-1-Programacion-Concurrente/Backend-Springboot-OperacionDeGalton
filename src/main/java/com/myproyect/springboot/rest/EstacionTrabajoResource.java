package com.myproyect.springboot.rest;

import com.myproyect.springboot.model.EstacionTrabajoDTO;
import com.myproyect.springboot.services.EstacionTrabajoService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estaciones-trabajo")
public class EstacionTrabajoResource {

    private final EstacionTrabajoService estacionTrabajoService;

    public EstacionTrabajoResource(final EstacionTrabajoService estacionTrabajoService) {
        this.estacionTrabajoService = estacionTrabajoService;
    }

    @GetMapping("/list")
    @ApiResponse(responseCode = "200", description = "Get all estaciones de trabajo")
    public ResponseEntity<List<EstacionTrabajoDTO>> getAllEstacionesTrabajo() {
        return ResponseEntity.ok(estacionTrabajoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstacionTrabajoDTO> getEstacionTrabajo(@PathVariable final Long id) {
        return ResponseEntity.ok(estacionTrabajoService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Create a new estacion de trabajo")
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
    @ApiResponse(responseCode = "204", description = "Delete a estacion de trabajo")
    public ResponseEntity<Void> deleteEstacionTrabajo(@PathVariable final Long id) {
        estacionTrabajoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

