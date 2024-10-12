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
        MaquinaDistribucionNormal maquina = maquinaDistribucionNormalRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        double media = maquina.getMedia();
        double desviacion = maquina.getDesviacionEstandar();
        int maxValue = maquina.getMaximoValor();
        int numSamples = 10000; // Ajustar para mayor precisión
        Map<String, Integer> distribucion = new HashMap<>();
        Random random = new Random();

        // Inicializar el mapa con todos los posibles valores.
        for (int x = -maxValue; x <= maxValue; x++) {
            distribucion.put("Valor_" + x, 0);
        }

        // Generar valores usando la distribución normal y contar las ocurrencias.
        for (int i = 0; i < numSamples; i++) {
            // Generar un valor siguiendo la distribución normal.
            double valor = media + desviacion * random.nextGaussian();

            // Redondear el valor para asignarlo a un contenedor.
            int contenedor = (int) Math.round(valor);

            // Asegurarse de que el valor esté dentro del rango [-maxValue, maxValue].
            if (contenedor >= -maxValue && contenedor <= maxValue) {
                String key = "Valor_" + contenedor;
                distribucion.put(key, distribucion.getOrDefault(key, 0) + 1);
            }
        }

        return distribucion;
    }

}



