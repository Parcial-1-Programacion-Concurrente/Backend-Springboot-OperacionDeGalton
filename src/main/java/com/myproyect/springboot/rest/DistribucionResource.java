package com.myproyect.springboot.rest;

import com.myproyect.springboot.model.DistribucionDTO;
import com.myproyect.springboot.service.DistribucionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/distribuciones")
public class DistribucionResource {

    private final DistribucionService distribucionService;

    public DistribucionResource(final DistribucionService distribucionService) {
        this.distribucionService = distribucionService;
    }

    @GetMapping("/list")
    @ApiResponse(responseCode = "200", description = "Get all distribuciones")
    public ResponseEntity<List<DistribucionDTO>> getAllDistribuciones() {
        return ResponseEntity.ok(distribucionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DistribucionDTO> getDistribucion(@PathVariable final Long id) {
        return ResponseEntity.ok(distribucionService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Create a new distribucion")
    public ResponseEntity<Long> createDistribucion(@RequestBody final DistribucionDTO distribucionDTO) {
        final Long createdId = distribucionService.create(distribucionDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDistribucion(@PathVariable final Long id,
                                                   @RequestBody final DistribucionDTO distribucionDTO) {
        distribucionService.update(id, distribucionDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Delete a distribucion")
    public ResponseEntity<Void> deleteDistribucion(@PathVariable final Long id) {
        distribucionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
