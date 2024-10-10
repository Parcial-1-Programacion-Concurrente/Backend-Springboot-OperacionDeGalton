package com.myproyect.springboot.services.maquinas;

import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionUniforme;
import com.myproyect.springboot.repos.ComponenteRepository;
import com.myproyect.springboot.repos.maquinasRepos.MaquinaDistribucionUniformeRepository;
import com.myproyect.springboot.repos.maquinasRepos.MaquinaRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MaquinaDistribucionUniformeService extends MaquinaService {

    private final MaquinaDistribucionUniformeRepository maquinaDistribucionUniformeRepository;

    public MaquinaDistribucionUniformeService(final MaquinaRepository maquinaRepository,
                                              final ComponenteRepository componenteRepository,
                                              final MaquinaDistribucionUniformeRepository maquinaDistribucionUniformeRepository) {
        super(maquinaRepository, componenteRepository);
        this.maquinaDistribucionUniformeRepository = maquinaDistribucionUniformeRepository;
    }

    @Override
    public Map<String, Integer> calcularDistribucion(Integer id) {
        MaquinaDistribucionUniforme maquina = (MaquinaDistribucionUniforme) maquinaDistribucionUniformeRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        int n = maquina.getNumValores();
        Map<String, Integer> distribucion = new HashMap<>();

        for (int i = 1; i <= n; i++) {
            distribucion.put("Valor_" + i, 100 / n); // Probabilidad uniforme
        }

        return distribucion;
    }
}

