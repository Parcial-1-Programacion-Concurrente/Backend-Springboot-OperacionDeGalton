package com.myproyect.springboot.controller;


import com.myproyect.springboot.model.FabricaGaussDTO;
import com.myproyect.springboot.service.FabricaGaussService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fabricas-gauss")
public class FabricaGaussController {

    private final FabricaGaussService fabricaGaussService;

    public FabricaGaussController(final FabricaGaussService fabricaGaussService) {
        this.fabricaGaussService = fabricaGaussService;
    }

    @GetMapping
    public ResponseEntity<List<FabricaGaussDTO>> getAllFabricasGauss() {
        return ResponseEntity.ok(fabricaGaussService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FabricaGaussDTO> getFabricaGauss(@PathVariable final Long id) {
        return ResponseEntity.ok(fabricaGaussService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createFabricaGauss(@RequestBody final FabricaGaussDTO fabricaGaussDTO) {
        final Long createdId = fabricaGaussService.create(fabricaGaussDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateFabricaGauss(@PathVariable final Long id,
                                                   @RequestBody final FabricaGaussDTO fabricaGaussDTO) {
        fabricaGaussService.update(id, fabricaGaussDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFabricaGauss(@PathVariable final Long id) {
        fabricaGaussService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
