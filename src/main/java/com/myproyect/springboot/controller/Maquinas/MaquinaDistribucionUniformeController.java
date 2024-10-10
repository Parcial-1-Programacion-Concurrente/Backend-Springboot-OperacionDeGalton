package com.myproyect.springboot.controller.Maquinas;

import com.myproyect.springboot.model.maquinas.MaquinaDistribucionUniformeDTO;
import com.myproyect.springboot.services.maquinas.MaquinaDistribucionUniformeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/maquinas-uniforme")
public class MaquinaDistribucionUniformeController {

    private final MaquinaDistribucionUniformeService maquinaDistribucionUniformeService;

    public MaquinaDistribucionUniformeController(final MaquinaDistribucionUniformeService maquinaDistribucionUniformeService) {
        this.maquinaDistribucionUniformeService = maquinaDistribucionUniformeService;
    }

    @GetMapping("/{id}/distribucion")
    public ResponseEntity<Map<String, Integer>> getDistribucionUniforme(@PathVariable final Integer id) {
        Map<String, Integer> distribucion = maquinaDistribucionUniformeService.calcularDistribucion(id);
        return ResponseEntity.ok(distribucion);
    }

    @PostMapping
    public ResponseEntity<Integer> createMaquina(@RequestBody MaquinaDistribucionUniformeDTO maquinaDTO) {
        Integer id = maquinaDistribucionUniformeService.create(maquinaDTO);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMaquina(@PathVariable final Integer id, @RequestBody final MaquinaDistribucionUniformeDTO maquinaDTO) {
        maquinaDistribucionUniformeService.update(id, maquinaDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaquina(@PathVariable final Integer id) {
        maquinaDistribucionUniformeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
