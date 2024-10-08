package com.myproyect.springboot.services;

import com.myproyect.springboot.model.LineaEnsamblajeDTO;
import com.myproyect.springboot.domain.concurrency.LineaEnsamblaje;
import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import com.myproyect.springboot.domain.factory.MaquinaFactory;
import com.myproyect.springboot.repos.LineaEnsamblajeRepository;
import com.myproyect.springboot.repos.MaquinaRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Service
public class LineaEnsamblajeService {

    private final LineaEnsamblajeRepository lineaEnsamblajeRepository;
    private final MaquinaRepository maquinaRepository;
    private final BlockingQueue<Componente> bufferCompartido;
    private final Semaphore semaforoComponentes;

    @Autowired
    public LineaEnsamblajeService(final LineaEnsamblajeRepository lineaEnsamblajeRepository,
                                  final MaquinaRepository maquinaRepository) {
        this.lineaEnsamblajeRepository = lineaEnsamblajeRepository;
        this.maquinaRepository = maquinaRepository;
        this.bufferCompartido = new LinkedBlockingQueue<>(10); // Capacidad máxima de 10 componentes.
        this.semaforoComponentes = new Semaphore(10); // Semáforo para controlar el acceso al buffer.
    }

    // Metodo para inicializar y ejecutar la línea de ensamblaje.
    public void iniciarEnsamblaje(Integer lineaEnsamblajeId, Maquina maquina) {
        LineaEnsamblaje lineaEnsamblaje = lineaEnsamblajeRepository.findById(lineaEnsamblajeId)
                .orElseThrow(() -> new NotFoundException("Línea de ensamblaje no encontrada con ID: " + lineaEnsamblajeId));

        // Configurar el buffer y el semáforo.
        lineaEnsamblaje.setBufferCompartido(new LinkedBlockingQueue<>(lineaEnsamblaje.getCapacidadBuffer()));
        lineaEnsamblaje.setSemaforoComponentes(new Semaphore(lineaEnsamblaje.getCapacidadBuffer()));

        // Ejecutar el ensamblaje en un nuevo hilo, pasando la máquina como parámetro.
        new Thread(() -> {
            try {
                lineaEnsamblaje.ensamblarMaquina(maquina);
            } catch (InterruptedException e) {
                System.err.println("El ensamblaje fue interrumpido: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }).start();
    }


    // Metodo para detener el ensamblaje de forma controlada
    public void detenerEnsamblaje(Integer lineaEnsamblajeId) {
        LineaEnsamblaje lineaEnsamblaje = lineaEnsamblajeRepository.findById(lineaEnsamblajeId)
                .orElseThrow(() -> new NotFoundException("Línea de ensamblaje no encontrada con ID: " + lineaEnsamblajeId));

        // Llamar al metodo de la línea de ensamblaje para marcar la interrupcion
        lineaEnsamblaje.detenerEnsamblaje();

        System.out.println("Se ha solicitado detener el ensamblaje para la línea de ensamblaje con ID: " + lineaEnsamblajeId);
    }



    public List<LineaEnsamblajeDTO> findAll() {
        return lineaEnsamblajeRepository.findAll(Sort.by("id")).stream()
                .map(linea -> mapToDTO(linea, new LineaEnsamblajeDTO()))
                .collect(Collectors.toList());
    }

    public LineaEnsamblajeDTO get(final Integer id) {
        return lineaEnsamblajeRepository.findById(id)
                .map(linea -> mapToDTO(linea, new LineaEnsamblajeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final LineaEnsamblajeDTO lineaEnsamblajeDTO) {
        LineaEnsamblaje lineaEnsamblaje = new LineaEnsamblaje();
        mapToEntity(lineaEnsamblajeDTO, lineaEnsamblaje);
        return lineaEnsamblajeRepository.save(lineaEnsamblaje).getId();
    }

    public void update(final Integer id, final LineaEnsamblajeDTO lineaEnsamblajeDTO) {
        LineaEnsamblaje lineaEnsamblaje = lineaEnsamblajeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(lineaEnsamblajeDTO, lineaEnsamblaje);
        lineaEnsamblajeRepository.save(lineaEnsamblaje);
    }

    public void delete(final Integer id) {
        lineaEnsamblajeRepository.deleteById(id);
    }

    // Métodos de mapeo entre la entidad y el DTO.
    private LineaEnsamblajeDTO mapToDTO(final LineaEnsamblaje lineaEnsamblaje, final LineaEnsamblajeDTO lineaEnsamblajeDTO) {
        lineaEnsamblajeDTO.setId(lineaEnsamblaje.getId());
        lineaEnsamblajeDTO.setCapacidadBuffer(lineaEnsamblaje.getCapacidadBuffer());
        return lineaEnsamblajeDTO;
    }

    private LineaEnsamblaje mapToEntity(final LineaEnsamblajeDTO lineaEnsamblajeDTO, final LineaEnsamblaje lineaEnsamblaje) {
        lineaEnsamblaje.setCapacidadBuffer(lineaEnsamblajeDTO.getCapacidadBuffer());
        return lineaEnsamblaje;
    }
}
