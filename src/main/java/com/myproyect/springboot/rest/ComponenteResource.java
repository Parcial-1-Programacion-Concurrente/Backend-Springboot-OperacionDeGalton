package com.myproyect.springboot.rest;

import com.myproyect.springboot.model.ComponenteDTO;
import com.myproyect.springboot.services.ComponenteService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/componentes")
public class ComponenteResource {

    private final ComponenteService componenteService;

    public ComponenteResource(final ComponenteService componenteService) {
        this.componenteService = componenteService;
    }

    @GetMapping("/list")
    @ApiResponse(responseCode = "200", description = "Get all componentes")
    public ResponseEntity<List<ComponenteDTO>> getAllComponentes() {
        return ResponseEntity.ok(componenteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComponenteDTO> getComponente(@PathVariable final Long id) {
        return ResponseEntity.ok(componenteService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Create a new componente")
    public ResponseEntity<Long> createComponente(@RequestBody final ComponenteDTO componenteDTO) {
        final Long createdId = componenteService.create(componenteDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateComponente(@PathVariable final Long id,
                                                 @RequestBody final ComponenteDTO componenteDTO) {
        componenteService.update(id, componenteDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Delete a componente")
    public ResponseEntity<Void> deleteComponente(@PathVariable final Long id) {
        componenteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

