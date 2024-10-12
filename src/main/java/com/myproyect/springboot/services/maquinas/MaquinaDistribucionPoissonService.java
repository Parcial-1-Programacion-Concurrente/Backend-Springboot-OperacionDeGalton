package com.myproyect.springboot.services.maquinas;

import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionPoisson;
import com.myproyect.springboot.repos.ComponenteRepository;
import com.myproyect.springboot.repos.maquinasRepos.MaquinaDistribucionPoissonRepository;
import com.myproyect.springboot.repos.maquinasRepos.MaquinaRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MaquinaDistribucionPoissonService extends MaquinaService {

    private final MaquinaDistribucionPoissonRepository maquinaDistribucionPoissonRepository;

    public MaquinaDistribucionPoissonService(final MaquinaRepository maquinaRepository,
                                             final ComponenteRepository componenteRepository,
                                             final MaquinaDistribucionPoissonRepository maquinaDistribucionPoissonRepository) {
        super(maquinaRepository, componenteRepository);
        this.maquinaDistribucionPoissonRepository = maquinaDistribucionPoissonRepository;
    }

    @Override
    public Map<String, Integer> calcularDistribucion(Integer id) {
        MaquinaDistribucionPoisson maquina = maquinaDistribucionPoissonRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        double lambda = maquina.getLambda();
        Map<String, Integer> distribucion = new HashMap<>();
        int escala = 10000;

        for (int k = 0; k <= maquina.getMaximoValor(); k++) {
            double probabilidad = (Math.pow(lambda, k) * Math.exp(-lambda)) / factorial(k);
            int valorEscalado = (int) Math.max(10, Math.round(probabilidad * escala));
            distribucion.put("Eventos_" + k, valorEscalado);
        }

        return distribucion;
    }

    private int factorial(int n) {
        if (n == 0) return 1;
        return n * factorial(n - 1);
    }
}


