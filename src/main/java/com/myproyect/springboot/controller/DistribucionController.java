package com.myproyect.springboot.controller;

import com.myproyect.springboot.model.DistribucionDTO;
import com.myproyect.springboot.services.DistribucionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/distribuciones")
public class DistribucionController {

    private final DistribucionService distribucionService;

    public DistribucionController(final DistribucionService distribucionService) {
        this.distribucionService = distribucionService;
    }

    @GetMapping
    public ResponseEntity<List<DistribucionDTO>> getAllDistribuciones() {
        return ResponseEntity.ok(distribucionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DistribucionDTO> getDistribucion(@PathVariable final Integer id) {
        return ResponseEntity.ok(distribucionService.get(id));
    }

    @PostMapping
    public ResponseEntity<Integer> createDistribucion(@RequestBody final DistribucionDTO distribucionDTO) {
        final Integer createdId = distribucionService.create(distribucionDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDistribucion(@PathVariable final Integer id, @RequestBody final DistribucionDTO distribucionDTO) {
        distribucionService.update(id, distribucionDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDistribucion(@PathVariable final Integer id) {
        distribucionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
