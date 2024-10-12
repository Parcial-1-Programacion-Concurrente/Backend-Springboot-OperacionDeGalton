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
        MaquinaDistribucionCustom maquina = maquinaDistribucionCustomRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        Map<String, Integer> distribucion = maquina.getProbabilidadesPersonalizadas();
        int total = distribucion.values().stream().mapToInt(Integer::intValue).sum();
        distribucion.replaceAll((k, v) -> (v * 100) / total);

        return distribucion;
    }
}

