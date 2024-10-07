package com.myproyect.springboot.services.maquinas;

import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionGeometrica;
import com.myproyect.springboot.repos.MaquinaRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MaquinaDistribucionGeometricaService extends MaquinaService {

    public MaquinaDistribucionGeometricaService(final MaquinaRepository maquinaRepository) {
        super(maquinaRepository);
    }

    @Override
    public Map<String, Integer> calcularDistribucion(Long id) {
        MaquinaDistribucionGeometrica maquina = (MaquinaDistribucionGeometrica) super.maquinaRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        double p = maquina.getProbabilidadExito(); // Probabilidad de éxito
        int maxIntentos = maquina.getMaximoEnsayos(); // Número máximo de intentos a considerar
        Map<String, Integer> distribucion = new HashMap<>();

        for (int k = 1; k <= maxIntentos; k++) {
            double probabilidad = Math.pow(1 - p, k - 1) * p;
            distribucion.put("Ensayo_" + k, (int) (probabilidad * 100)); // Convertir a porcentaje
        }

        return distribucion;
    }
}
