package com.myproyect.springboot.services;

import com.myproyect.springboot.model.ComponenteDTO;
import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.repos.ComponenteRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComponenteService {

    private final ComponenteRepository componenteRepository;

    public ComponenteService(final ComponenteRepository componenteRepository) {
        this.componenteRepository = componenteRepository;
    }

    public List<ComponenteDTO> findAll() {
        return componenteRepository.findAll(Sort.by("id")).stream()
                .map(componente -> mapToDTO(componente, new ComponenteDTO()))
                .collect(Collectors.toList());
    }

    public ComponenteDTO get(final Integer id) {
        return componenteRepository.findById(id)
                .map(componente -> mapToDTO(componente, new ComponenteDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ComponenteDTO componenteDTO) {
        Componente componente = new Componente();
        mapToEntity(componenteDTO, componente);
        return componenteRepository.save(componente).getId();
    }

    public void update(final Integer id, final ComponenteDTO componenteDTO) {
        Componente componente = componenteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(componenteDTO, componente);
        componenteRepository.save(componente);
    }

    public void delete(final Integer id) {
        componenteRepository.deleteById(id);
    }

    private ComponenteDTO mapToDTO(final Componente componente, final ComponenteDTO componenteDTO) {
        componenteDTO.setTipo(componente.getTipo());
        componenteDTO.setValorCalculado(componente.getValorCalculado());
        return componenteDTO;
    }

    private Componente mapToEntity(final ComponenteDTO componenteDTO, final Componente componente) {
        componente.setTipo(componenteDTO.getTipo());
        componente.setValorCalculado(componenteDTO.getValorCalculado());
        return componente;
    }
}

