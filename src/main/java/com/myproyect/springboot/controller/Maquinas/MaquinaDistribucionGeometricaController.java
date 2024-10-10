package com.myproyect.springboot.controller.Maquinas;

import com.myproyect.springboot.model.maquinas.MaquinaDistribucionGeometricaDTO;
import com.myproyect.springboot.services.maquinas.MaquinaDistribucionGeometricaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/maquinas-geometrica")
public class MaquinaDistribucionGeometricaController {

    private final MaquinaDistribucionGeometricaService maquinaDistribucionGeometricaService;

    public MaquinaDistribucionGeometricaController(final MaquinaDistribucionGeometricaService maquinaDistribucionGeometricaService) {
        this.maquinaDistribucionGeometricaService = maquinaDistribucionGeometricaService;
    }

    @GetMapping("/{id}/distribucion")
    public ResponseEntity<Map<String, Integer>> getDistribucionGeometrica(@PathVariable final Integer id) {
        Map<String, Integer> distribucion = maquinaDistribucionGeometricaService.calcularDistribucion(id);
        return ResponseEntity.ok(distribucion);
    }

    @PostMapping
    public ResponseEntity<Integer> createMaquina(@RequestBody MaquinaDistribucionGeometricaDTO maquinaDTO) {
        Integer id = maquinaDistribucionGeometricaService.create(maquinaDTO);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMaquina(@PathVariable final Integer id, @RequestBody final MaquinaDistribucionGeometricaDTO maquinaDTO) {
        maquinaDistribucionGeometricaService.update(id, maquinaDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaquina(@PathVariable final Integer id) {
        maquinaDistribucionGeometricaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
