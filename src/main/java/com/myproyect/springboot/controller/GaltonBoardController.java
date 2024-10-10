package com.myproyect.springboot.controller;

import com.myproyect.springboot.model.GaltonBoardDTO;
import com.myproyect.springboot.services.GaltonBoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/galton-boards")
public class GaltonBoardController {

    private final GaltonBoardService galtonBoardService;

    public GaltonBoardController(final GaltonBoardService galtonBoardService) {
        this.galtonBoardService = galtonBoardService;
    }

    @GetMapping
    public ResponseEntity<List<GaltonBoardDTO>> getAllGaltonBoards() {
        return ResponseEntity.ok(galtonBoardService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GaltonBoardDTO> getGaltonBoard(@PathVariable final Integer id) {
        return ResponseEntity.ok(galtonBoardService.get(id));
    }

    @PostMapping("/simular/{id}")
    public ResponseEntity<Void> simularCaidaBolas(@PathVariable Integer id) {
        galtonBoardService.simularCaidaDeBolas(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/mostrar-distribucion/{id}")
    public ResponseEntity<Void> mostrarDistribucion(@PathVariable Integer id) {
        galtonBoardService.mostrarDistribucion(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Integer> createGaltonBoard(@RequestBody final GaltonBoardDTO galtonBoardDTO) {
        final Integer createdId = galtonBoardService.create(galtonBoardDTO).getId();
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateGaltonBoard(@PathVariable final Integer id, @RequestBody final GaltonBoardDTO galtonBoardDTO) {
        galtonBoardService.update(id, galtonBoardDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGaltonBoard(@PathVariable final Integer id) {
        galtonBoardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
