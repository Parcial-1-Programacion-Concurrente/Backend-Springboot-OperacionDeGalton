package com.myproyect.springboot.controller;

import com.myproyect.springboot.model.DistribucionDTO;
import com.myproyect.springboot.services.DistribucionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/distribucion")
public class DistribucionController {

    @Autowired
    private DistribucionService distribucionService;

    @GetMapping("/{id}")
    public ResponseEntity<DistribucionDTO> getDistribucion(@PathVariable Integer id) {
        DistribucionDTO distribucionDTO = distribucionService.getDistribucionDTO(id);
        return ResponseEntity.ok(distribucionDTO);
    }
}
