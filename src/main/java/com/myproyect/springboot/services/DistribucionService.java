package com.myproyect.springboot.services;

import com.myproyect.springboot.model.DistribucionDTO;
import com.myproyect.springboot.domain.distribution.Distribucion;
import com.myproyect.springboot.repos.DistribucionRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DistribucionService {

    private final DistribucionRepository distribucionRepository;

    public DistribucionService(final DistribucionRepository distribucionRepository) {
        this.distribucionRepository = distribucionRepository;
    }

    public List<DistribucionDTO> findAll() {
        return distribucionRepository.findAll(Sort.by("id")).stream()
                .map(distribucion -> mapToDTO(distribucion, new DistribucionDTO()))
                .collect(Collectors.toList());
    }

    public DistribucionDTO get(final Integer id) {
        return distribucionRepository.findById(id)
                .map(distribucion -> mapToDTO(distribucion, new DistribucionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final DistribucionDTO distribucionDTO) {
        Distribucion distribucion = new Distribucion();
        mapToEntity(distribucionDTO, distribucion);
        return distribucionRepository.save(distribucion).getId();
    }

    public void update(final Integer id, final DistribucionDTO distribucionDTO) {
        Distribucion distribucion = distribucionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(distribucionDTO, distribucion);
        distribucionRepository.save(distribucion);
    }

    public void delete(final Integer id) {
        distribucionRepository.deleteById(id);
    }

    private DistribucionDTO mapToDTO(final Distribucion distribucion, final DistribucionDTO distribucionDTO) {
        distribucionDTO.setId(distribucion.getId());
        distribucionDTO.setDatos(distribucion.getDatos());
        distribucionDTO.setNumBolas(distribucion.getNumBolas());
        distribucionDTO.setNumContenedores(distribucion.getNumContenedores());
        return distribucionDTO;
    }

    private Distribucion mapToEntity(final DistribucionDTO distribucionDTO, final Distribucion distribucion) {
        distribucion.setDatos(distribucionDTO.getDatos());
        distribucion.setNumBolas(distribucionDTO.getNumBolas());
        distribucion.setNumContenedores(distribucionDTO.getNumContenedores());
        return distribucion;
    }
}
