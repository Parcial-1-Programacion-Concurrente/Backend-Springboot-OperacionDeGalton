package com.myproyect.springboot.services.maquinas;

import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionGeometrica;
import com.myproyect.springboot.repos.ComponenteRepository;
import com.myproyect.springboot.repos.maquinasRepos.MaquinaDistribucionGeometricaRepository;
import com.myproyect.springboot.repos.maquinasRepos.MaquinaRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MaquinaDistribucionGeometricaService extends MaquinaService {

    private final MaquinaDistribucionGeometricaRepository maquinaDistribucionGeometricaRepository;

    public MaquinaDistribucionGeometricaService(final MaquinaRepository maquinaRepository,
                                                final ComponenteRepository componenteRepository,
                                                final MaquinaDistribucionGeometricaRepository maquinaDistribucionGeometricaRepository) {
        super(maquinaRepository, componenteRepository);
        this.maquinaDistribucionGeometricaRepository = maquinaDistribucionGeometricaRepository;
    }

    @Override
    public Map<String, Integer> calcularDistribucion(Integer id) {
        MaquinaDistribucionGeometrica maquina = maquinaDistribucionGeometricaRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        double p = maquina.getProbabilidadExito();
        int maxIntentos = maquina.getMaximoEnsayos();
        Map<String, Integer> distribucion = new HashMap<>();

        for (int k = 1; k <= maxIntentos; k++) {
            double probabilidad = Math.pow(1 - p, k - 1) * p;
            distribucion.put("Ensayo_" + k, (int) (probabilidad * 100));
        }

        return distribucion;
    }
}
