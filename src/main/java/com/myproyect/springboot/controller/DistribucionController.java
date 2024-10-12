package com.myproyect.springboot.controller;

import com.myproyect.springboot.model.DistribucionDTO;
import com.myproyect.springboot.services.DistribucionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/distribucion")
public class DistribucionController {

    private final DistribucionService distribucionService;

    public DistribucionController(final DistribucionService distribucionService) {
        this.distribucionService = distribucionService;
    }

    @GetMapping
    @ApiResponse(responseCode = "200", description = "Get all distributions")
    @ResponseBody
    public ResponseEntity<List<DistribucionDTO>> getAllDistribuciones() {
        return ResponseEntity.ok(distribucionService.findAll());
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Get a distribution by ID")
    @ResponseBody
    public ResponseEntity<DistribucionDTO> getDistribucionById(@PathVariable final Integer id) {
        return ResponseEntity.ok(distribucionService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Create a new distribution")
    @ResponseBody
    public ResponseEntity<Integer> createDistribucion(@RequestBody final DistribucionDTO distribucionDTO) {
        Integer createdId = distribucionService.create(distribucionDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Update a distribution")
    @ResponseBody
    public ResponseEntity<Void> updateDistribucion(@PathVariable final Integer id, @RequestBody final DistribucionDTO distribucionDTO) {
        distribucionService.update(id, distribucionDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Delete a distribution")
    @ResponseBody
    public ResponseEntity<Void> deleteDistribucion(@PathVariable final Integer id) {
        distribucionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}