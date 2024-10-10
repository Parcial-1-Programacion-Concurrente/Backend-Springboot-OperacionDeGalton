package com.myproyect.springboot.controller.Maquinas;

import com.myproyect.springboot.model.maquinas.MaquinaDistribucionPoissonDTO;
import com.myproyect.springboot.services.maquinas.MaquinaDistribucionPoissonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/maquinas-poisson")
public class MaquinaDistribucionPoissonController {

    private final MaquinaDistribucionPoissonService maquinaDistribucionPoissonService;

    public MaquinaDistribucionPoissonController(final MaquinaDistribucionPoissonService maquinaDistribucionPoissonService) {
        this.maquinaDistribucionPoissonService = maquinaDistribucionPoissonService;
    }

    @GetMapping("/{id}/distribucion")
    public ResponseEntity<Map<String, Integer>> getDistribucionPoisson(@PathVariable final Integer id) {
        Map<String, Integer> distribucion = maquinaDistribucionPoissonService.calcularDistribucion(id);
        return ResponseEntity.ok(distribucion);
    }

    @PostMapping
    public ResponseEntity<Integer> createMaquina(@RequestBody MaquinaDistribucionPoissonDTO maquinaDTO) {
        Integer id = maquinaDistribucionPoissonService.create(maquinaDTO);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMaquina(@PathVariable final Integer id, @RequestBody final MaquinaDistribucionPoissonDTO maquinaDTO) {
        maquinaDistribucionPoissonService.update(id, maquinaDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaquina(@PathVariable final Integer id) {
        maquinaDistribucionPoissonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
