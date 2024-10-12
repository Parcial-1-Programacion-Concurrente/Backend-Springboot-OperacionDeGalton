package com.myproyect.springboot.controller;

import com.myproyect.springboot.model.DistribucionDTO;
import com.myproyect.springboot.model.GaltonBoardDTO;
import com.myproyect.springboot.services.GaltonBoardService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/galton-board")
public class GaltonBoardController {

    private final GaltonBoardService galtonBoardService;

    public GaltonBoardController(final GaltonBoardService galtonBoardService) {
        this.galtonBoardService = galtonBoardService;
    }

    @GetMapping
    @ApiResponse(responseCode = "200", description = "Get all Galton boards")
    @ResponseBody
    public ResponseEntity<List<GaltonBoardDTO>> getAllGaltonBoards() {
        return ResponseEntity.ok(galtonBoardService.findAll());
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Get a specific Galton board by ID")
    @ResponseBody
    public ResponseEntity<GaltonBoardDTO> getGaltonBoardById(@PathVariable final Integer id) {
        return ResponseEntity.ok(galtonBoardService.get(id));
    }

    @GetMapping("/{id}/distribucion")
    @ApiResponse(responseCode = "200", description = "Get the distribution of a specific Galton board")
    @ResponseBody
    public ResponseEntity<DistribucionDTO> getDistribucionForGaltonBoard(@PathVariable final Integer id) {
        GaltonBoardDTO galtonBoard = galtonBoardService.get(id);
        if (galtonBoard.getDistribucion() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(galtonBoard.getDistribucion());
    }
}
