package com.myproyect.springboot.rest;

import com.myproyect.springboot.model.LineaEnsamblajeDTO;
import com.myproyect.springboot.services.LineaEnsamblajeService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lineas-ensamblaje")
public class LineaEnsamblajeResource {

    private final LineaEnsamblajeService lineaEnsamblajeService;

    public LineaEnsamblajeResource(final LineaEnsamblajeService lineaEnsamblajeService) {
        this.lineaEnsamblajeService = lineaEnsamblajeService;
    }

    @GetMapping("/list")
    @ApiResponse(responseCode = "200", description = "Get all lineas de ensamblaje")
    public ResponseEntity<List<LineaEnsamblajeDTO>> getAllLineasEnsamblaje() {
        return ResponseEntity.ok(lineaEnsamblajeService.findAll());
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Get a linea de ensamblaje by ID")
    public ResponseEntity<LineaEnsamblajeDTO> getLineaEnsamblaje(@PathVariable final Long id) {
        return ResponseEntity.ok(lineaEnsamblajeService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Create a new linea de ensamblaje")
    public ResponseEntity<Long> createLineaEnsamblaje(@RequestBody final LineaEnsamblajeDTO lineaEnsamblajeDTO) {
        final Long createdId = lineaEnsamblajeService.create(lineaEnsamblajeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Update an existing linea de ensamblaje")
    public ResponseEntity<Void> updateLineaEnsamblaje(@PathVariable final Long id,
                                                      @RequestBody final LineaEnsamblajeDTO lineaEnsamblajeDTO) {
        lineaEnsamblajeService.update(id, lineaEnsamblajeDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Delete a linea de ensamblaje")
    public ResponseEntity<Void> deleteLineaEnsamblaje(@PathVariable final Long id) {
        lineaEnsamblajeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

