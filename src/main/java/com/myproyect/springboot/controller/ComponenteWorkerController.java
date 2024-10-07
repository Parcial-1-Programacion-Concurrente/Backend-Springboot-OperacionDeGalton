package com.myproyect.springboot.controller;


import com.myproyect.springboot.model.ComponenteWorkerDTO;
import com.myproyect.springboot.service.ComponenteWorkerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/componente-workers")
public class ComponenteWorkerController {

    private final ComponenteWorkerService componenteWorkerService;

    public ComponenteWorkerController(final ComponenteWorkerService componenteWorkerService) {
        this.componenteWorkerService = componenteWorkerService;
    }

    @GetMapping
    public ResponseEntity<List<ComponenteWorkerDTO>> getAllComponenteWorkers() {
        return ResponseEntity.ok(componenteWorkerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComponenteWorkerDTO> getComponenteWorker(@PathVariable final Long id) {
        return ResponseEntity.ok(componenteWorkerService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createComponenteWorker(@RequestBody final ComponenteWorkerDTO componenteWorkerDTO) {
        final Long createdId = componenteWorkerService.create(componenteWorkerDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateComponenteWorker(@PathVariable final Long id,
                                                       @RequestBody final ComponenteWorkerDTO componenteWorkerDTO) {
        componenteWorkerService.update(id, componenteWorkerDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComponenteWorker(@PathVariable final Long id) {
        componenteWorkerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
