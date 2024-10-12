package com.myproyect.springboot.controller;


import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import com.myproyect.springboot.services.FabricaGaussService;
import com.myproyect.springboot.services.maquinas.MaquinaService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/fabrica-gaus")
public class FabricaGaussController {

    private final FabricaGaussService fabricaGaussService;
    private final MaquinaService maquinaService;

    public FabricaGaussController(final FabricaGaussService fabricaGaussService, final MaquinaService maquinaService) {
        this.fabricaGaussService = fabricaGaussService;
        this.maquinaService = maquinaService;
    }

    @PostMapping("/iniciar")
    @ApiResponse(responseCode = "200", description = "Start the simulation process")
    @ResponseBody
    public ResponseEntity<Void> iniciarProduccion() {
        fabricaGaussService.iniciarProduccion();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/detener")
    @ApiResponse(responseCode = "200", description = "Stop the simulation process")
    @ResponseBody
    public ResponseEntity<Void> detenerSimulacion() {
        fabricaGaussService.detenerSimulacion();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/estado")
    @ApiResponse(responseCode = "200", description = "Get the current state of the machine")
    @ResponseBody
    public ResponseEntity<String> obtenerEstado(@PathVariable final Integer id) {
        String estado = maquinaService.obtenerEstadoMaquina(id);
        return ResponseEntity.ok(estado);
    }
}