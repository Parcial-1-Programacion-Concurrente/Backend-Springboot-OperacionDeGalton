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
import java.util.stream.Collectors;

@Service
public class ComponenteWorkerService implements Runnable {

    private final ComponenteWorkerRepository componenteWorkerRepository;

    public ComponenteWorkerService(final ComponenteWorkerRepository componenteWorkerRepository) {
        this.componenteWorkerRepository = componenteWorkerRepository;
    }

    public List<ComponenteWorkerDTO> findAll() {
        return componenteWorkerRepository.findAll(Sort.by("id")).stream()
                .map(worker -> mapToDTO(worker, new ComponenteWorkerDTO()))
                .collect(Collectors.toList());
    }

    public ComponenteWorkerDTO get(final Long id) {
        return componenteWorkerRepository.findById(id)
                .map(worker -> mapToDTO(worker, new ComponenteWorkerDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ComponenteWorkerDTO componenteWorkerDTO) {
        ComponenteWorker componenteWorker = new ComponenteWorker();
        mapToEntity(componenteWorkerDTO, componenteWorker);
        return componenteWorkerRepository.save(componenteWorker).getId();
    }

    public void update(final Long id, final ComponenteWorkerDTO componenteWorkerDTO) {
        ComponenteWorker componenteWorker = componenteWorkerRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(componenteWorkerDTO, componenteWorker);
        componenteWorkerRepository.save(componenteWorker);
    }

    public void delete(final Long id) {
        componenteWorkerRepository.deleteById(id);
    }

    @Override
    public void run() {
        calcularValor();
    }

    public void calcularValor() {

    }

    private ComponenteWorkerDTO mapToDTO(final ComponenteWorker worker, final ComponenteWorkerDTO dto) {
        dto.setId(worker.getId());
        return dto;
    }

    private ComponenteWorker mapToEntity(final ComponenteWorkerDTO dto, final ComponenteWorker worker) {
        return worker;
    }
}

