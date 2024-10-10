package com.myproyect.springboot.controller;

import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import com.myproyect.springboot.model.MaquinaWorkerDTO;
import com.myproyect.springboot.repos.GaltonBoardRepository;
import com.myproyect.springboot.repos.maquinasRepos.MaquinaRepository;
import com.myproyect.springboot.services.MaquinaWorkerService;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maquinas-worker")
public class MaquinaWorkerController {

    private final MaquinaWorkerService maquinaWorkerService;

    @Autowired
    private MaquinaRepository maquinaRepository;

    @Autowired
    private GaltonBoardRepository galtonBoardRepository;

    public MaquinaWorkerController(final MaquinaWorkerService maquinaWorkerService) {
        this.maquinaWorkerService = maquinaWorkerService;
    }

    // Obtener todos los MaquinaWorkers
    @GetMapping
    public ResponseEntity<List<MaquinaWorkerDTO>> getAllMaquinaWorkers() {
        return ResponseEntity.ok(maquinaWorkerService.findAll());
    }

    // Obtener un MaquinaWorker por su ID
    @GetMapping("/{id}")
    public ResponseEntity<MaquinaWorkerDTO> getMaquinaWorker(@PathVariable final Integer id) {
        return ResponseEntity.ok(maquinaWorkerService.get(id));
    }

    // Iniciar trabajo para una máquina y un GaltonBoard
    @PostMapping("/iniciar/{maquinaId}/{galtonBoardId}")
    public ResponseEntity<Void> iniciarTrabajo(@PathVariable Integer maquinaId, @PathVariable Integer galtonBoardId) {
        // Buscar la máquina a partir del ID
        Maquina maquina = maquinaRepository.findById(maquinaId)
                .orElseThrow(() -> new NotFoundException("Maquina no encontrada con ID: " + maquinaId));

        // Buscar el GaltonBoard a partir del ID
        GaltonBoard galtonBoard = galtonBoardRepository.findById(galtonBoardId)
                .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + galtonBoardId));

        // Iniciar el trabajo con las entidades Maquina y GaltonBoard
        maquinaWorkerService.iniciarTrabajo(maquina, galtonBoard);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Eliminar un MaquinaWorker por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaquinaWorker(@PathVariable final Integer id) {
        maquinaWorkerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Ensamblar una máquina
    @GetMapping("/ensamblar/{id}")
    public ResponseEntity<Void> ensamblarMaquina(@PathVariable final Integer id) {
        MaquinaWorkerDTO maquinaWorkerDTO = maquinaWorkerService.get(id);

        // Convertir el DTO a una entidad antes de pasar al servicio
        maquinaWorkerService.ensamblarMaquina(maquinaWorkerService.obtenerMaquinaWorker(maquinaWorkerDTO.getId()));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

