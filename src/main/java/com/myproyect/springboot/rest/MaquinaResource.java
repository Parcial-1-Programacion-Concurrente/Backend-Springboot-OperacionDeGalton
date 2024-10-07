package com.myproyect.springboot.rest;

import com.myproyect.springboot.model.maquinas.MaquinaDTO;
import com.myproyect.springboot.services.maquinas.MaquinaService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maquinas")
public class MaquinaResource {

    private final MaquinaService maquinaService;

    public MaquinaResource(final MaquinaService maquinaService) {
        this.maquinaService = maquinaService;
    }

    @GetMapping("/list")
    @ApiResponse(responseCode = "200", description = "Get all maquinas")
    public ResponseEntity<List<MaquinaDTO>> getAllMaquinas() {
        return ResponseEntity.ok(maquinaService.findAll());
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Get a maquina by ID")
    public ResponseEntity<MaquinaDTO> getMaquina(@PathVariable final Long id) {
        return ResponseEntity.ok(maquinaService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Create a new maquina")
    public ResponseEntity<Long> createMaquina(@RequestBody final MaquinaDTO maquinaDTO) {
        final Long createdId = maquinaService.create(maquinaDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Update an existing maquina")
    public ResponseEntity<Void> updateMaquina(@PathVariable final Long id,
                                              @RequestBody final MaquinaDTO maquinaDTO) {
        maquinaService.update(id, maquinaDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Delete a maquina")
    public ResponseEntity<Void> deleteMaquina(@PathVariable final Long id) {
        maquinaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
