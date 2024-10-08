package com.myproyect.springboot.services.maquinas;

import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionBinomial;
import com.myproyect.springboot.repos.ComponenteRepository;
import com.myproyect.springboot.repos.MaquinaRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MaquinaDistribucionBinomialService extends MaquinaService {

    public MaquinaDistribucionBinomialService(final MaquinaRepository maquinaRepository,
                                              final ComponenteRepository componenteRepository) {
        super(maquinaRepository, componenteRepository);
    }

    @Override
    public Map<String, Integer> calcularDistribucion(Integer id) {
        MaquinaDistribucionBinomial maquina = (MaquinaDistribucionBinomial) super.getMaquinaRepository().findById(id)
                .orElseThrow(NotFoundException::new);

        int n = maquina.getNumEnsayos(); // Número de ensayos
        double p = maquina.getProbabilidadExito(); // Probabilidad de éxito
        Map<String, Integer> distribucion = new HashMap<>();

        for (int k = 0; k <= n; k++) {
            double probabilidad = calcularBinomial(n, k, p);
            distribucion.put("Éxitos_" + k, (int) (probabilidad * 100)); // Probabilidad en porcentaje
        }

        return distribucion;
    }

    private double calcularBinomial(int n, int k, double p) {
        return combinar(n, k) * Math.pow(p, k) * Math.pow(1 - p, n - k);
    }

    private int combinar(int n, int k) {
        if (k == 0 || k == n) return 1;
        return combinar(n - 1, k - 1) + combinar(n - 1, k);
    }
}

