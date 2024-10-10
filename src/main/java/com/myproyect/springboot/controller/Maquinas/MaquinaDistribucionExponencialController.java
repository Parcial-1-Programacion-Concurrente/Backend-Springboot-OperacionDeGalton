package com.myproyect.springboot.controller.Maquinas;

import com.myproyect.springboot.model.maquinas.MaquinaDistribucionExponencialDTO;
import com.myproyect.springboot.services.maquinas.MaquinaDistribucionExponencialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/maquinas-exponencial")
public class MaquinaDistribucionExponencialController {

    private final MaquinaDistribucionExponencialService maquinaDistribucionExponencialService;

    public MaquinaDistribucionExponencialController(final MaquinaDistribucionExponencialService maquinaDistribucionExponencialService) {
        this.maquinaDistribucionExponencialService = maquinaDistribucionExponencialService;
    }

    @GetMapping("/{id}/distribucion")
    public ResponseEntity<Map<String, Integer>> getDistribucionExponencial(@PathVariable final Integer id) {
        Map<String, Integer> distribucion = maquinaDistribucionExponencialService.calcularDistribucion(id);
        return ResponseEntity.ok(distribucion);
    }

    @PostMapping
    public ResponseEntity<Integer> createMaquina(@RequestBody MaquinaDistribucionExponencialDTO maquinaDTO) {
        Integer id = maquinaDistribucionExponencialService.create(maquinaDTO);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMaquina(@PathVariable final Integer id, @RequestBody final MaquinaDistribucionExponencialDTO maquinaDTO) {
        maquinaDistribucionExponencialService.update(id, maquinaDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaquina(@PathVariable final Integer id) {
        maquinaDistribucionExponencialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
