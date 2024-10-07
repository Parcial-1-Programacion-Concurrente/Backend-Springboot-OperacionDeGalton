package com.myproyect.springboot.services.maquinas;

import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionUniforme;
import com.myproyect.springboot.repos.ComponenteRepository;
import com.myproyect.springboot.repos.MaquinaRepository;
import com.myproyect.springboot.services.maquinas.MaquinaService;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MaquinaDistribucionUniformeService extends MaquinaService {

    public MaquinaDistribucionUniformeService(final MaquinaRepository maquinaRepository,
                                              final ComponenteRepository componenteRepository) {
        super(maquinaRepository, componenteRepository);
    }

    @Override
    public Map<String, Integer> calcularDistribucion(Long id) {
        MaquinaDistribucionUniforme maquina = (MaquinaDistribucionUniforme) super.getMaquinaRepository().findById(id)
                .orElseThrow(NotFoundException::new);

        int n = maquina.getNumValores();
        Map<String, Integer> distribucion = new HashMap<>();

        for (int i = 1; i <= n; i++) {
            distribucion.put("Valor_" + i, 100 / n); // Probabilidad uniforme
        }

        return distribucion;
    }
}

