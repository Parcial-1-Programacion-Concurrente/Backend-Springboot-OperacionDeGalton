package com.myproyect.springboot.services;

import com.myproyect.springboot.model.FabricaGaussDTO;
import com.myproyect.springboot.domain.concurrency.FabricaGauss;
import com.myproyect.springboot.domain.concurrency.EstacionTrabajo;
import com.myproyect.springboot.domain.concurrency.LineaEnsamblaje;
import com.myproyect.springboot.repos.FabricaGaussRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class FabricaGaussService {

    private final FabricaGaussRepository fabricaGaussRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Autowired
    public FabricaGaussService(final FabricaGaussRepository fabricaGaussRepository) {
        this.fabricaGaussRepository = fabricaGaussRepository;
    }

    public List<FabricaGaussDTO> findAll() {
        return fabricaGaussRepository.findAll(Sort.by("id")).stream()
                .map(fabrica -> mapToDTO(fabrica, new FabricaGaussDTO()))
                .collect(Collectors.toList());
    }

    public FabricaGaussDTO get(final Long id) {
        return fabricaGaussRepository.findById(id)
                .map(fabrica -> mapToDTO(fabrica, new FabricaGaussDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final FabricaGaussDTO fabricaGaussDTO) {
        FabricaGauss fabricaGauss = new FabricaGauss();
        mapToEntity(fabricaGaussDTO, fabricaGauss);
        return fabricaGaussRepository.save(fabricaGauss).getId();
    }

    public void update(final Long id, final FabricaGaussDTO fabricaGaussDTO) {
        FabricaGauss fabricaGauss = fabricaGaussRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(fabricaGaussDTO, fabricaGauss);
        fabricaGaussRepository.save(fabricaGauss);
    }

    public void delete(final Long id) {
        fabricaGaussRepository.deleteById(id);
    }

    public void iniciarProduccion(FabricaGauss fabricaGauss) {
        fabricaGauss.getEstaciones().forEach(estacion -> executorService.submit(estacion));
        executorService.submit(fabricaGauss.getLineaEnsamblaje());
    }

    public void detenerProduccion() {
        executorService.shutdownNow();
    }

    public void asignarTareas(FabricaGauss fabricaGauss) {
        fabricaGauss.getEstaciones().forEach(estacion -> {
            // Implementar lógica de scheduling específica (Round Robin, etc.)
            executorService.submit(estacion);
        });
    }

    private FabricaGaussDTO mapToDTO(final FabricaGauss fabricaGauss, final FabricaGaussDTO fabricaGaussDTO) {
        fabricaGaussDTO.setId(fabricaGauss.getId());
        fabricaGaussDTO.setNumEstaciones(fabricaGauss.getNumEstaciones());
        return fabricaGaussDTO;
    }

    private FabricaGauss mapToEntity(final FabricaGaussDTO fabricaGaussDTO, final FabricaGauss fabricaGauss) {
        fabricaGauss.setNumEstaciones(fabricaGaussDTO.getNumEstaciones());
        return fabricaGauss;
    }
}
