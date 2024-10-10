package com.myproyect.springboot.services.maquinas;

import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionNormal;
import com.myproyect.springboot.repos.ComponenteRepository;
import com.myproyect.springboot.repos.maquinasRepos.MaquinaDistribucionNormalRepository;
import com.myproyect.springboot.repos.maquinasRepos.MaquinaRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Primary
public class MaquinaDistribucionNormalService extends MaquinaService {

    private final MaquinaDistribucionNormalRepository maquinaDistribucionNormalRepository;

    public MaquinaDistribucionNormalService(final MaquinaRepository maquinaRepository,
                                            final ComponenteRepository componenteRepository,
                                            final MaquinaDistribucionNormalRepository maquinaDistribucionNormalRepository) {
        super(maquinaRepository, componenteRepository);
        this.maquinaDistribucionNormalRepository = maquinaDistribucionNormalRepository;
    }

    @Override
    public Map<String, Integer> calcularDistribucion(Integer id) {
        MaquinaDistribucionNormal maquina = (MaquinaDistribucionNormal) maquinaDistribucionNormalRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        double media = maquina.getMedia(); // μ (media)
        double desviacion = maquina.getDesviacionEstandar(); // σ (desviación estándar)
        int maxValue = maquina.getMaximoValor();
        Map<String, Integer> distribucion = new HashMap<>();

        for (int x = -maxValue; x <= maxValue; x++) {
            double probabilidad = (1 / (desviacion * Math.sqrt(2 * Math.PI))) *
                    Math.exp(-Math.pow(x - media, 2) / (2 * Math.pow(desviacion, 2)));
            distribucion.put("Valor_" + x, (int) (probabilidad * 100)); // Convertir a porcentaje
        }

        return distribucion;
    }
}


