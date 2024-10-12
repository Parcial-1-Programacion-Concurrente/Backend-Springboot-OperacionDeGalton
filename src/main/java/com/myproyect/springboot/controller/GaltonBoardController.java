package com.myproyect.springboot.controller;

import com.myproyect.springboot.model.DistribucionDTO;
import com.myproyect.springboot.model.GaltonBoardDTO;
import com.myproyect.springboot.services.GaltonBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/galton-board")
public class GaltonBoardController {

    @Autowired
    private GaltonBoardService galtonBoardService;

    @GetMapping("/list")
    public ResponseEntity<List<GaltonBoardDTO>> getAllGaltonBoards() {
        List<GaltonBoardDTO> galtonBoards = galtonBoardService.findAll();
        return ResponseEntity.ok(galtonBoards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GaltonBoardDTO> getGaltonBoard(@PathVariable Integer id) {
        GaltonBoardDTO galtonBoardDTO = galtonBoardService.getGaltonBoardDTO(id);
        return ResponseEntity.ok(galtonBoardDTO);
    }

    @PostMapping
    public ResponseEntity<GaltonBoardDTO> createGaltonBoard(@RequestBody GaltonBoardDTO galtonBoardDTO) {
        GaltonBoardDTO createdGaltonBoard = galtonBoardService.createGaltonBoard(galtonBoardDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGaltonBoard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGaltonBoard(@PathVariable Integer id) {
        galtonBoardService.deleteGaltonBoard(id);
        return ResponseEntity.noContent().build();
    }
}
