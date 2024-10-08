package com.myproyect.springboot.services.maquinas;

import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionExponencial;
import com.myproyect.springboot.repos.ComponenteRepository;
import com.myproyect.springboot.repos.MaquinaRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MaquinaDistribucionExponencialService extends MaquinaService {

    public MaquinaDistribucionExponencialService(final MaquinaRepository maquinaRepository,
                                                 final ComponenteRepository componenteRepository) {
        super(maquinaRepository, componenteRepository);
    }

    @Override
    public Map<String, Integer> calcularDistribucion(Integer id) {
        MaquinaDistribucionExponencial maquina = (MaquinaDistribucionExponencial) super.getMaquinaRepository().findById(id)
                .orElseThrow(NotFoundException::new);

        double lambda = maquina.getLambda(); // Tasa de eventos (lambda)
        int maxValue = maquina.getMaximoValor();
        Map<String, Integer> distribucion = new HashMap<>();

        for (int x = 0; x <= maxValue; x++) {
            double probabilidad = lambda * Math.exp(-lambda * x);
            distribucion.put("Valor_" + x, (int) (probabilidad * 100)); // Convertir a porcentaje
        }

        return distribucion;
    }
}

