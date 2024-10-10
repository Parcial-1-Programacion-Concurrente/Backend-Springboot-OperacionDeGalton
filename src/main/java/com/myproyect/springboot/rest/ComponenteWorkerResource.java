package com.myproyect.springboot.rest;

import com.myproyect.springboot.model.ComponenteWorkerDTO;
import com.myproyect.springboot.services.ComponenteWorkerService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/componente-workers")
public class ComponenteWorkerResource {

    private final ComponenteWorkerService componenteWorkerService;

    public ComponenteWorkerResource(final ComponenteWorkerService componenteWorkerService) {
        this.componenteWorkerService = componenteWorkerService;
    }

    @GetMapping("/list")
    @ApiResponse(responseCode = "200", description = "Get all componente workers")
    public ResponseEntity<List<ComponenteWorkerDTO>> getAllComponenteWorkers() {
        return ResponseEntity.ok(componenteWorkerService.findAll());
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Get a componente worker by ID")
    public ResponseEntity<ComponenteWorkerDTO> getComponenteWorker(@PathVariable final Integer id) {
        return ResponseEntity.ok(componenteWorkerService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Create a new componente worker")
    public ResponseEntity<Integer> createComponenteWorker(@RequestBody final ComponenteWorkerDTO componenteWorkerDTO) {
        final Integer createdId = componenteWorkerService.create(componenteWorkerDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Update an existing componente worker")
    public ResponseEntity<Void> updateComponenteWorker(@PathVariable final Integer id,
                                                       @RequestBody final ComponenteWorkerDTO componenteWorkerDTO) {
        componenteWorkerService.update(id, componenteWorkerDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Delete a componente worker")
    public ResponseEntity<Void> deleteComponenteWorker(@PathVariable final Integer id) {
        componenteWorkerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

