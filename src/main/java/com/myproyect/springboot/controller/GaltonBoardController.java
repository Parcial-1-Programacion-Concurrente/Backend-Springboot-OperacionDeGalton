package com.myproyect.springboot.controller;

import com.myproyect.springboot.domain.synchronization.GaltonBoardStatus;
import com.myproyect.springboot.model.GaltonBoardDTO;
import com.myproyect.springboot.model.GaltonBoardStatusDTO;
import com.myproyect.springboot.services.GaltonBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/galton-board")
public class GaltonBoardController {

    @Autowired
    private GaltonBoardService galtonBoardService;

    @GetMapping
    public ResponseEntity<List<GaltonBoardStatusDTO>> getAllGaltonBoards() {
        List<GaltonBoardStatus> galtonBoardStatuses = galtonBoardService.getAllGaltonBoards();
        List<GaltonBoardStatusDTO> statusDTOs = galtonBoardStatuses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(statusDTOs);
    }

    private GaltonBoardStatusDTO mapToDTO(GaltonBoardStatus status) {
        GaltonBoardStatusDTO dto = new GaltonBoardStatusDTO();
        dto.setId(status.getId());
        dto.setEstado(status.getEstado());
        dto.setDistribucionActual(status.getDistribucionActual());
        return dto;
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

    @GetMapping("/{id}/status")
    public ResponseEntity<GaltonBoardStatusDTO> getGaltonBoardStatus(@PathVariable Integer id) {
        GaltonBoardStatus galtonBoardStatus = galtonBoardService.getGaltonBoardStatus(id);
        GaltonBoardStatusDTO statusDTO = mapToDTO(galtonBoardStatus);
        return ResponseEntity.ok(statusDTO);
    }

}
