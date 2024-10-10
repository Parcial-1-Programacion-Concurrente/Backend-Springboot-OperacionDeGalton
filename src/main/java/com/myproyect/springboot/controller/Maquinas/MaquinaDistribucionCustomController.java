package com.myproyect.springboot.controller.Maquinas;

import com.myproyect.springboot.model.maquinas.MaquinaDistribucionCustomDTO;
import com.myproyect.springboot.services.maquinas.MaquinaDistribucionCustomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/maquinas-custom")
public class MaquinaDistribucionCustomController {

    private final MaquinaDistribucionCustomService maquinaDistribucionCustomService;

    public MaquinaDistribucionCustomController(final MaquinaDistribucionCustomService maquinaDistribucionCustomService) {
        this.maquinaDistribucionCustomService = maquinaDistribucionCustomService;
    }

    @GetMapping("/{id}/distribucion")
    public ResponseEntity<Map<String, Integer>> getDistribucionCustom(@PathVariable final Integer id) {
        Map<String, Integer> distribucion = maquinaDistribucionCustomService.calcularDistribucion(id);
        return ResponseEntity.ok(distribucion);
    }

    @PostMapping
    public ResponseEntity<Integer> createMaquina(@RequestBody MaquinaDistribucionCustomDTO maquinaDTO) {
        Integer id = maquinaDistribucionCustomService.create(maquinaDTO);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMaquina(@PathVariable final Integer id, @RequestBody final MaquinaDistribucionCustomDTO maquinaDTO) {
        maquinaDistribucionCustomService.update(id, maquinaDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaquina(@PathVariable final Integer id) {
        maquinaDistribucionCustomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
