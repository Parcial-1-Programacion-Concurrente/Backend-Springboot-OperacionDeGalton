package com.myproyect.springboot.services;

import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.domain.concurrency.EstacionTrabajo;
import com.myproyect.springboot.domain.concurrency.FabricaGauss;
import com.myproyect.springboot.model.EstacionTrabajoDTO;
import com.myproyect.springboot.repos.EstacionTrabajoRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Service
public class EstacionTrabajoService {

    private final EstacionTrabajoRepository estacionTrabajoRepository;

    @Autowired
    public EstacionTrabajoService(final EstacionTrabajoRepository estacionTrabajoRepository) {
        this.estacionTrabajoRepository = estacionTrabajoRepository;
    }

    // Iniciar la producción de componentes en una estación de trabajo.
    public void iniciarProduccion(Integer estacionId) {
        EstacionTrabajo estacion = estacionTrabajoRepository.findById(estacionId)
                .orElseThrow(() -> new NotFoundException("Estación de trabajo no encontrada con ID: " + estacionId));

        // Inicializar el buffer de componentes.
        estacion.setBufferComponentes(new LinkedBlockingQueue<>(estacion.getCapacidadBuffer()));

        // Iniciar la estación de trabajo en un nuevo hilo.
        new Thread(estacion).start();
        System.out.println("Producción iniciada en la estación de trabajo: " + estacion.getNombre());
    }

    // Detener la producción de la estación de trabajo.
    public void detenerProduccion(Integer estacionId) {
        System.out.println("La producción ha sido detenida para la estación de trabajo con ID: " + estacionId);
        // En este metodo se podria implementar una forma de notificar al hilo de la estación para que se detenga.
    }

    // Métodos CRUD para gestionar EstacionTrabajo.
    public List<EstacionTrabajoDTO> findAll() {
        return estacionTrabajoRepository.findAll(Sort.by("id")).stream()
                .map(estacion -> mapToDTO(estacion, new EstacionTrabajoDTO()))
                .collect(Collectors.toList());
    }

    public EstacionTrabajoDTO get(final Integer id) {
        return estacionTrabajoRepository.findById(id)
                .map(estacion -> mapToDTO(estacion, new EstacionTrabajoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final EstacionTrabajoDTO estacionTrabajoDTO) {
        EstacionTrabajo estacionTrabajo = new EstacionTrabajo();
        mapToEntity(estacionTrabajoDTO, estacionTrabajo);
        return estacionTrabajoRepository.save(estacionTrabajo).getId();
    }

    public void update(final Integer id, final EstacionTrabajoDTO estacionTrabajoDTO) {
        EstacionTrabajo estacionTrabajo = estacionTrabajoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(estacionTrabajoDTO, estacionTrabajo);
        estacionTrabajoRepository.save(estacionTrabajo);
    }

    public void delete(final Integer id) {
        estacionTrabajoRepository.deleteById(id);
    }

    private EstacionTrabajoDTO mapToDTO(final EstacionTrabajo estacion, final EstacionTrabajoDTO dto) {
        dto.setId(estacion.getId());
        dto.setNombre(estacion.getNombre());
        dto.setTipo(estacion.getTipo());
        dto.setCapacidadBuffer(estacion.getCapacidadBuffer());
        dto.setFabricaGaussId(estacion.getFabricaGauss().getId());
        return dto;
    }

    private EstacionTrabajo mapToEntity(final EstacionTrabajoDTO dto, final EstacionTrabajo estacion) {
        estacion.setNombre(dto.getNombre());
        estacion.setTipo(dto.getTipo());
        estacion.setCapacidadBuffer(dto.getCapacidadBuffer());
        return estacion;
    }
}

