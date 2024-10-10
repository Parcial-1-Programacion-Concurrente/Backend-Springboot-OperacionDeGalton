package com.myproyect.springboot.controller.Maquinas;

import com.myproyect.springboot.model.maquinas.MaquinaDistribucionNormalDTO;
import com.myproyect.springboot.services.maquinas.MaquinaDistribucionNormalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/maquinas-normal")
public class MaquinaDistribucionNormalController {

    private final MaquinaDistribucionNormalService maquinaDistribucionNormalService;

    public MaquinaDistribucionNormalController(final MaquinaDistribucionNormalService maquinaDistribucionNormalService) {
        this.maquinaDistribucionNormalService = maquinaDistribucionNormalService;
    }

    @GetMapping("/{id}/distribucion")
    public ResponseEntity<Map<String, Integer>> getDistribucionNormal(@PathVariable final Integer id) {
        Map<String, Integer> distribucion = maquinaDistribucionNormalService.calcularDistribucion(id);
        return ResponseEntity.ok(distribucion);
    }

    @PostMapping
    public ResponseEntity<Integer> createMaquina(@RequestBody MaquinaDistribucionNormalDTO maquinaDTO) {
        Integer id = maquinaDistribucionNormalService.create(maquinaDTO);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMaquina(@PathVariable final Integer id, @RequestBody final MaquinaDistribucionNormalDTO maquinaDTO) {
        maquinaDistribucionNormalService.update(id, maquinaDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaquina(@PathVariable final Integer id) {
        maquinaDistribucionNormalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
