package com.myproyect.springboot.rest;

import com.myproyect.springboot.model.FabricaGaussDTO;
import com.myproyect.springboot.services.FabricaGaussService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fabricas-gauss")
public class FabricaGaussResource {

    private final FabricaGaussService fabricaGaussService;

    public FabricaGaussResource(final FabricaGaussService fabricaGaussService) {
        this.fabricaGaussService = fabricaGaussService;
    }

    @GetMapping("/list")
    @ApiResponse(responseCode = "200", description = "Get all fabricas gauss")
    public ResponseEntity<List<FabricaGaussDTO>> getAllFabricasGauss() {
        return ResponseEntity.ok(fabricaGaussService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FabricaGaussDTO> getFabricaGauss(@PathVariable final Long id) {
        return ResponseEntity.ok(fabricaGaussService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Create a new fabrica gauss")
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
    @ApiResponse(responseCode = "204", description = "Delete a fabrica gauss")
    public ResponseEntity<Void> deleteFabricaGauss(@PathVariable final Long id) {
        fabricaGaussService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

