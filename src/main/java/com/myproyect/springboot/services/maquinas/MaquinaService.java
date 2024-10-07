package com.myproyect.springboot.services.maquinas;

import com.myproyect.springboot.model.MaquinaDTO;
import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import com.myproyect.springboot.repos.MaquinaRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public abstract class MaquinaService {

    private final MaquinaRepository maquinaRepository;

    public MaquinaService(final MaquinaRepository maquinaRepository) {
        this.maquinaRepository = maquinaRepository;
    }

    public List<MaquinaDTO> findAll() {
        return maquinaRepository.findAll(Sort.by("id")).stream()
                .map(maquina -> mapToDTO(maquina, new MaquinaDTO()))
                .collect(Collectors.toList());
    }

    public MaquinaDTO get(final Long id) {
        return maquinaRepository.findById(id)
                .map(maquina -> mapToDTO(maquina, new MaquinaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final MaquinaDTO maquinaDTO) {
        Maquina maquina = new Maquina();
        mapToEntity(maquinaDTO, maquina);
        return maquinaRepository.save(maquina).getId();
    }

    public void update(final Long id, final MaquinaDTO maquinaDTO) {
        Maquina maquina = maquinaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(maquinaDTO, maquina);
        maquinaRepository.save(maquina);
    }

    public void delete(final Long id) {
        maquinaRepository.deleteById(id);
    }

    private MaquinaDTO mapToDTO(final Maquina maquina, final MaquinaDTO maquinaDTO) {
        maquinaDTO.setId(maquina.getId());
        return maquinaDTO;
    }

    private Maquina mapToEntity(final MaquinaDTO maquinaDTO, final Maquina maquina) {
        return maquina;
    }

    public boolean validarComponentes(Maquina maquina) {
        return maquina.validarComponentes();
    }

    public abstract Map<String, Integer> calcularDistribucion(Long id);
}

