package com.myproyect.springboot.services;

import com.myproyect.springboot.domain.concurrency.ComponenteWorker;
import com.myproyect.springboot.model.MaquinaWorkerDTO;
import com.myproyect.springboot.domain.concurrency.MaquinaWorker;
import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.repos.MaquinaWorkerRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
public class MaquinaWorkerService implements Runnable {

    private final MaquinaWorkerRepository maquinaWorkerRepository;

    public MaquinaWorkerService(final MaquinaWorkerRepository maquinaWorkerRepository) {
        this.maquinaWorkerRepository = maquinaWorkerRepository;
    }

    public List<MaquinaWorkerDTO> findAll() {
        return maquinaWorkerRepository.findAll(Sort.by("id")).stream()
                .map(worker -> mapToDTO(worker, new MaquinaWorkerDTO()))
                .collect(Collectors.toList());
    }

    public MaquinaWorkerDTO get(final Long id) {
        return maquinaWorkerRepository.findById(id)
                .map(worker -> mapToDTO(worker, new MaquinaWorkerDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final MaquinaWorkerDTO maquinaWorkerDTO) {
        MaquinaWorker maquinaWorker = new MaquinaWorker();
        mapToEntity(maquinaWorkerDTO, maquinaWorker);
        return maquinaWorkerRepository.save(maquinaWorker).getId();
    }

    public void update(final Long id, final MaquinaWorkerDTO maquinaWorkerDTO) {
        MaquinaWorker maquinaWorker = maquinaWorkerRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(maquinaWorkerDTO, maquinaWorker);
        maquinaWorkerRepository.save(maquinaWorker);
    }

    public void delete(final Long id) {
        maquinaWorkerRepository.deleteById(id);
    }

    @Override
    public void run() {
        calcularDistribucion();
    }

    public Map<String, Integer> calcularDistribucion() {

    }

    public void agregarComponenteWorker(ComponenteWorker worker) {
    }

    public void ensamblarMaquina() {
        // Lógica para ensamblar la máquina una vez que todos los ComponenteWorkers han terminado su trabajo.
    }

    private MaquinaWorkerDTO mapToDTO(final MaquinaWorker worker, final MaquinaWorkerDTO dto) {
        dto.setId(worker.getId());
        return dto;
    }

    private MaquinaWorker mapToEntity(final MaquinaWorkerDTO dto, final MaquinaWorker worker) {
        return worker;
    }
}
