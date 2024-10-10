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
import java.util.Random;

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

        double media = maquina.getMedia();
        double desviacion = maquina.getDesviacionEstandar();
        int maxValue = maquina.getMaximoValor();
        Map<String, Integer> distribucion = new HashMap<>();
        int escala = 10000; // Ajusta la escala según sea necesario.
        int incrementoMinimo = 1; // Asegura que los valores sean siempre distintos.

        for (int x = -maxValue; x <= maxValue; x++) {
            double probabilidad = (1 / (desviacion * Math.sqrt(2 * Math.PI))) *
                    Math.exp(-Math.pow(x - media, 2) / (2 * Math.pow(desviacion, 2)));
            int valorEscalado = (int) Math.round(probabilidad * escala) + incrementoMinimo;

            // Aumenta el valor mínimo para la siguiente iteración
            incrementoMinimo++;

            // Asegurar que no haya valores negativos ni iguales a cero.
            valorEscalado = Math.max(1, valorEscalado);

            distribucion.put("Valor_" + x, valorEscalado);
        }

        return distribucion;
    }
}


