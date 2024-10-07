package com.myproyect.springboot.controller;


import com.myproyect.springboot.model.GaltonBoardDTO;
import com.myproyect.springboot.service.GaltonBoardService;
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
    public ResponseEntity<GaltonBoardDTO> getGaltonBoard(@PathVariable final Long id) {
        return ResponseEntity.ok(galtonBoardService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createGaltonBoard(@RequestBody final GaltonBoardDTO galtonBoardDTO) {
        final Long createdId = galtonBoardService.create(galtonBoardDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateGaltonBoard(@PathVariable final Long id,
                                                  @RequestBody final GaltonBoardDTO galtonBoardDTO) {
        galtonBoardService.update(id, galtonBoardDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGaltonBoard(@PathVariable final Long id) {
        galtonBoardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
