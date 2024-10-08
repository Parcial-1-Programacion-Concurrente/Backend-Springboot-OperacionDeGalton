package com.myproyect.springboot.services;

import com.myproyect.springboot.model.ComponenteWorkerDTO;
import com.myproyect.springboot.domain.concurrency.ComponenteWorker;
import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import com.myproyect.springboot.repos.ComponenteWorkerRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Service
public class ComponenteWorkerService {

    private final ComponenteWorkerRepository componenteWorkerRepository;

    public ComponenteWorkerService(final ComponenteWorkerRepository componenteWorkerRepository) {
        this.componenteWorkerRepository = componenteWorkerRepository;
    }

    public void runComponenteWorker(Integer componenteWorkerId, GaltonBoard galtonBoard, CountDownLatch latch) {
        ComponenteWorker componenteWorker = componenteWorkerRepository.findById(componenteWorkerId)
                .orElseThrow(() -> new NotFoundException("ComponenteWorker no encontrado con id: " + componenteWorkerId));

        // Configurar el GaltonBoard antes de ejecutar el cálculo.
        componenteWorker.setGaltonBoard(galtonBoard);

        // Ejecutar la lógica del cálculo de valor en un hilo.
        Thread workerThread = new Thread(() -> {
            try {
                componenteWorker.run();
            } finally {
                latch.countDown(); // Indicar que este worker ha terminado.
            }
        });
        workerThread.start();
    }


    public List<ComponenteWorkerDTO> findAll() {
        return componenteWorkerRepository.findAll(Sort.by("id")).stream()
                .map(worker -> mapToDTO(worker, new ComponenteWorkerDTO()))
                .collect(Collectors.toList());
    }

    public ComponenteWorkerDTO get(final Integer id) {
        return componenteWorkerRepository.findById(id)
                .map(worker -> mapToDTO(worker, new ComponenteWorkerDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ComponenteWorkerDTO componenteWorkerDTO) {
        ComponenteWorker componenteWorker = new ComponenteWorker();
        mapToEntity(componenteWorkerDTO, componenteWorker);
        return componenteWorkerRepository.save(componenteWorker).getId();
    }

    public void update(final Integer id, final ComponenteWorkerDTO componenteWorkerDTO) {
        ComponenteWorker componenteWorker = componenteWorkerRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(componenteWorkerDTO, componenteWorker);
        componenteWorkerRepository.save(componenteWorker);
    }

    public void delete(final Integer id) {
        componenteWorkerRepository.deleteById(id);
    }

    private ComponenteWorkerDTO mapToDTO(final ComponenteWorker worker, final ComponenteWorkerDTO dto) {
        dto.setId(worker.getId());
        return dto;
    }

    private ComponenteWorker mapToEntity(final ComponenteWorkerDTO dto, final ComponenteWorker worker) {
        return worker;
    }
}

