package com.myproyect.springboot.services;

import com.myproyect.springboot.model.LineaEnsamblajeDTO;
import com.myproyect.springboot.domain.concurrency.LineaEnsamblaje;
import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import com.myproyect.springboot.domain.factory.MaquinaFactory;
import com.myproyect.springboot.repos.LineaEnsamblajeRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Service
public class LineaEnsamblajeService implements Runnable {

    private final LineaEnsamblajeRepository lineaEnsamblajeRepository;

    public LineaEnsamblajeService(final LineaEnsamblajeRepository lineaEnsamblajeRepository) {
        this.lineaEnsamblajeRepository = lineaEnsamblajeRepository;
    }

    public List<LineaEnsamblajeDTO> findAll() {
        return lineaEnsamblajeRepository.findAll(Sort.by("id")).stream()
                .map(linea -> mapToDTO(linea, new LineaEnsamblajeDTO()))
                .collect(Collectors.toList());
    }

    public LineaEnsamblajeDTO get(final Long id) {
        return lineaEnsamblajeRepository.findById(id)
                .map(linea -> mapToDTO(linea, new LineaEnsamblajeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final LineaEnsamblajeDTO lineaEnsamblajeDTO) {
        LineaEnsamblaje lineaEnsamblaje = new LineaEnsamblaje();
        mapToEntity(lineaEnsamblajeDTO, lineaEnsamblaje);
        return lineaEnsamblajeRepository.save(lineaEnsamblaje).getId();
    }

    public void update(final Long id, final LineaEnsamblajeDTO lineaEnsamblajeDTO) {
        LineaEnsamblaje lineaEnsamblaje = lineaEnsamblajeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(lineaEnsamblajeDTO, lineaEnsamblaje);
        lineaEnsamblajeRepository.save(lineaEnsamblaje);
    }

    public void delete(final Long id) {
        lineaEnsamblajeRepository.deleteById(id);
    }

    @Override
    public void run() {
        try {
            ensamblarMaquina();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void ensamblarMaquina() throws InterruptedException {

    }

    public Componente consumirComponente(BlockingQueue<Componente> bufferCompartido) throws InterruptedException {

    }

    public void notificarProgreso(Maquina maquina) {
        // Implementar la lógica para notificar el progreso del ensamblaje de la máquina.
    }

    private LineaEnsamblajeDTO mapToDTO(final LineaEnsamblaje lineaEnsamblaje, final LineaEnsamblajeDTO lineaEnsamblajeDTO) {
        lineaEnsamblajeDTO.setId(lineaEnsamblaje.getId());
        lineaEnsamblajeDTO.setCapacidadBuffer(lineaEnsamblaje.getCapacidadBuffer());
        lineaEnsamblajeDTO.setTiempoEnsamblaje(lineaEnsamblaje.getTiempoEnsamblaje());
        return lineaEnsamblajeDTO;
    }

    private LineaEnsamblaje mapToEntity(final LineaEnsamblajeDTO lineaEnsamblajeDTO, final LineaEnsamblaje lineaEnsamblaje) {
        lineaEnsamblaje.setCapacidadBuffer(lineaEnsamblajeDTO.getCapacidadBuffer());
        lineaEnsamblaje.setTiempoEnsamblaje(lineaEnsamblajeDTO.getTiempoEnsamblaje());
        return lineaEnsamblaje;
    }
}
