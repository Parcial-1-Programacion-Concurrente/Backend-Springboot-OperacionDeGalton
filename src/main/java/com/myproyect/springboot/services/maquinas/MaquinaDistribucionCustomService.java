package com.myproyect.springboot.services.maquinas;

import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionCustom;
import com.myproyect.springboot.repos.ComponenteRepository;
import com.myproyect.springboot.repos.maquinasRepos.MaquinaDistribucionCustomRepository;
import com.myproyect.springboot.repos.maquinasRepos.MaquinaRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MaquinaDistribucionCustomService extends MaquinaService {

    private final MaquinaDistribucionCustomRepository maquinaDistribucionCustomRepository;

    public MaquinaDistribucionCustomService(final MaquinaRepository maquinaRepository,
                                            final ComponenteRepository componenteRepository,
                                            final MaquinaDistribucionCustomRepository maquinaDistribucionCustomRepository) {
        super(maquinaRepository, componenteRepository);
        this.maquinaDistribucionCustomRepository = maquinaDistribucionCustomRepository;

    }

    @Override
    public Map<String, Integer> calcularDistribucion(Integer id) {
        MaquinaDistribucionCustom maquina = (MaquinaDistribucionCustom) maquinaDistribucionCustomRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        // Supongamos que el usuario ha definido una lista de probabilidades personalizadas
        Map<String, Integer> distribucion = maquina.getProbabilidadesPersonalizadas();

        // Normalización o ajuste si es necesario para que la suma sea igual a 100%
        int total = distribucion.values().stream().mapToInt(Integer::intValue).sum();
        distribucion.replaceAll((k, v) -> (v * 100) / total);

        return distribucion;
    }
}

