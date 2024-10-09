package com.myproyect.springboot.rest;

import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import com.myproyect.springboot.model.GaltonBoardDTO;
import com.myproyect.springboot.services.GaltonBoardService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/galton-boards")
public class GaltonBoardResource {

    private final GaltonBoardService galtonBoardService;

    public GaltonBoardResource(final GaltonBoardService galtonBoardService) {
        this.galtonBoardService = galtonBoardService;
    }

    @GetMapping("/list")
    @ApiResponse(responseCode = "200", description = "Get all Galton boards")
    public ResponseEntity<List<GaltonBoardDTO>> getAllGaltonBoards() {
        return ResponseEntity.ok(galtonBoardService.findAll());
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Get a Galton board by ID")
    public ResponseEntity<GaltonBoardDTO> getGaltonBoard(@PathVariable final Integer id) {
        return ResponseEntity.ok(galtonBoardService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Create a new Galton board")
    public ResponseEntity<Integer> createGaltonBoard(@RequestBody final GaltonBoardDTO galtonBoardDTO) {
        final GaltonBoard galtonBoard = galtonBoardService.create(galtonBoardDTO);
        final Integer createdId = galtonBoard.getId();
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Update an existing Galton board")
    public ResponseEntity<Void> updateGaltonBoard(@PathVariable final Integer id,
                                                  @RequestBody final GaltonBoardDTO galtonBoardDTO) {
        galtonBoardService.update(id, galtonBoardDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Delete a Galton board")
    public ResponseEntity<Void> deleteGaltonBoard(@PathVariable final Integer id) {
        galtonBoardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

