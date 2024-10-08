package com.myproyect.springboot.domain.concurrency;

import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import org.hibernate.annotations.GenericGenerator;

import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

@Entity
@Table(name = "componentoe_workers")
@Getter
@Setter
public class ComponenteWorker implements Runnable {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "componente_id", nullable = false)
    private Componente componente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maquina_id", nullable = false)
    private Maquina maquina;

    @Transient
    private GaltonBoard galtonBoard;

    @Transient
    private final Logger logger = Logger.getLogger(ComponenteWorker.class.getName());

    @Getter
    @Transient
    private boolean trabajoCompletado = false;

    @Override
    public void run() {
        try {
            // Antes de calcular, verifica que el GaltonBoard esté configurado y que la simulación haya finalizado.
            if (galtonBoard == null || !galtonBoard.getEstado().equals("FINALIZADA")) {
                throw new IllegalStateException("El GaltonBoard debe estar presente y la simulación debe estar finalizada.");
            }

            double valorCalculado = calcularValor();
            componente.setValorCalculado(valorCalculado);
            trabajoCompletado = true;
            System.out.println("Cálculo de valor completado para el componente de tipo " + componente.getTipo() +
                    " con valor: " + valorCalculado);
        } catch (Exception e) {
            System.err.println("Error durante el cálculo del valor: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Calcula el valor del componente en función de la distribución del GaltonBoard.
     *
     * @return El valor calculado para el componente.
     */
    private double calcularValor() {
        // Verifica que el GaltonBoard esté presente y tenga una distribución asociada.
        if (galtonBoard == null || galtonBoard.getDistribucion() == null) {
            throw new IllegalStateException("El GaltonBoard y su Distribución deben estar configurados.");
        }

        // Obtiene la distribución de la simulación.
        var distribucion = galtonBoard.getDistribucion().getDatos();

        // Calcula un valor promedio basado en la distribución.
        double valorCalculado = 0.0;
        int totalBolas = galtonBoard.getNumBolas();

        // Itera sobre cada contenedor y calcula un promedio ponderado.
        for (var entry : distribucion.entrySet()) {
            String contenedor = entry.getKey();
            int bolasEnContenedor = entry.getValue();

            // Extrae el índice del contenedor desde el nombre (ej. "Contenedor 1").
            int indiceContenedor = Integer.parseInt(contenedor.replace("Contenedor ", ""));

            // Calcula el valor ponderado y lo suma al total.
            valorCalculado += indiceContenedor * ((double) bolasEnContenedor / totalBolas);
        }

        System.out.println("Valor calculado para el componente de tipo " + componente.getTipo() + ": " + valorCalculado);
        return valorCalculado;
    }

}


