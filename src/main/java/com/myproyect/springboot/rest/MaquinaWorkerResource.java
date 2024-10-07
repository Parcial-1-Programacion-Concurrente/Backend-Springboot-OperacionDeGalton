package com.myproyect.springboot.rest;

import com.myproyect.springboot.model.MaquinaWorkerDTO;
import com.myproyect.springboot.service.MaquinaWorkerService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maquina-workers")
public class MaquinaWorkerResource {

    private final MaquinaWorkerService maquinaWorkerService;

    public MaquinaWorkerResource(final MaquinaWorkerService maquinaWorkerService) {
        this.maquinaWorkerService = maquinaWorkerService;
    }

    @GetMapping("/list")
    @ApiResponse(responseCode = "200", description = "Get all maquina workers")
    public ResponseEntity<List<MaquinaWorkerDTO>> getAllMaquinaWorkers() {
        return ResponseEntity.ok(maquinaWorkerService.findAll());
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Get a maquina worker by ID")
    public ResponseEntity<MaquinaWorkerDTO> getMaquinaWorker(@PathVariable final Long id) {
        return ResponseEntity.ok(maquinaWorkerService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Create a new maquina worker")
    public ResponseEntity<Long> createMaquinaWorker(@RequestBody final MaquinaWorkerDTO maquinaWorkerDTO) {
        final Long createdId = maquinaWorkerService.create(maquinaWorkerDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Update an existing maquina worker")
    public ResponseEntity<Void> updateMaquinaWorker(@PathVariable final Long id,
                                                    @RequestBody final MaquinaWorkerDTO maquinaWorkerDTO) {
        maquinaWorkerService.update(id, maquinaWorkerDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Delete a maquina worker")
    public ResponseEntity<Void> deleteMaquinaWorker(@PathVariable final Long id) {
        maquinaWorkerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

