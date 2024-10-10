package com.myproyect.springboot.controller.Maquinas;

import com.myproyect.springboot.model.maquinas.MaquinaDistribucionBinomialDTO;
import com.myproyect.springboot.services.maquinas.MaquinaDistribucionBinomialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/maquinas-binomial")
public class MaquinaDistribucionBinomialController {

    private final MaquinaDistribucionBinomialService maquinaDistribucionBinomialService;

    public MaquinaDistribucionBinomialController(final MaquinaDistribucionBinomialService maquinaDistribucionBinomialService) {
        this.maquinaDistribucionBinomialService = maquinaDistribucionBinomialService;
    }

    @GetMapping("/{id}/distribucion")
    public ResponseEntity<Map<String, Integer>> getDistribucionBinomial(@PathVariable final Integer id) {
        Map<String, Integer> distribucion = maquinaDistribucionBinomialService.calcularDistribucion(id);
        return ResponseEntity.ok(distribucion);
    }

    @PostMapping
    public ResponseEntity<Integer> createMaquina(@RequestBody MaquinaDistribucionBinomialDTO maquinaDTO) {
        Integer id = maquinaDistribucionBinomialService.create(maquinaDTO);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMaquina(@PathVariable final Integer id, @RequestBody final MaquinaDistribucionBinomialDTO maquinaDTO) {
        maquinaDistribucionBinomialService.update(id, maquinaDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaquina(@PathVariable final Integer id) {
        maquinaDistribucionBinomialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
