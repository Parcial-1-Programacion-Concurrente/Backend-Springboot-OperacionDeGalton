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
        MaquinaDistribucionPoisson maquina = (MaquinaDistribucionPoisson) maquinaDistribucionPoissonRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        double lambda = maquina.getLambda();
        Map<String, Integer> distribucion = new HashMap<>();

        for (int k = 0; k <= maquina.getMaximoValor(); k++) {
            double probabilidad = (Math.pow(lambda, k) * Math.exp(-lambda)) / factorial(k);
            distribucion.put("Eventos_" + k, (int) (probabilidad * 100)); // Probabilidad en porcentaje
        }

        return distribucion;
    }

    private int factorial(int n) {
        if (n == 0) return 1;
        return n * factorial(n - 1);
    }
}

