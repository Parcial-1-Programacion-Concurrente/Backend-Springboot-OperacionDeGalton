package com.myproyect.springboot.controller;


import com.myproyect.springboot.model.ComponenteDTO;
import com.myproyect.springboot.service.ComponenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/componentes")
public class ComponenteController {

    private final ComponenteService componenteService;

    public ComponenteController(final ComponenteService componenteService) {
        this.componenteService = componenteService;
    }

    @GetMapping
    public ResponseEntity<List<ComponenteDTO>> getAllComponentes() {
        return ResponseEntity.ok(componenteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComponenteDTO> getComponente(@PathVariable final Long id) {
        return ResponseEntity.ok(componenteService.get(id));
    }

    @PostMapping
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
    public ResponseEntity<Void> deleteComponente(@PathVariable final Long id) {
        componenteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
