package com.myproyect.springboot.controller;


import com.myproyect.springboot.model.LineaEnsamblajeDTO;
import com.myproyect.springboot.service.LineaEnsamblajeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lineas-ensamblaje")
public class LineaEnsamblajeController {

    private final LineaEnsamblajeService lineaEnsamblajeService;

    public LineaEnsamblajeController(final LineaEnsamblajeService lineaEnsamblajeService) {
        this.lineaEnsamblajeService = lineaEnsamblajeService;
    }

    @GetMapping
    public ResponseEntity<List<LineaEnsamblajeDTO>> getAllLineasEnsamblaje() {
        return ResponseEntity.ok(lineaEnsamblajeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineaEnsamblajeDTO> getLineaEnsamblaje(@PathVariable final Long id) {
        return ResponseEntity.ok(lineaEnsamblajeService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createLineaEnsamblaje(@RequestBody final LineaEnsamblajeDTO lineaEnsamblajeDTO) {
        final Long createdId = lineaEnsamblajeService.create(lineaEnsamblajeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLineaEnsamblaje(@PathVariable final Long id,
                                                      @RequestBody final LineaEnsamblajeDTO lineaEnsamblajeDTO) {
        lineaEnsamblajeService.update(id, lineaEnsamblajeDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLineaEnsamblaje(@PathVariable final Long id) {
        lineaEnsamblajeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
