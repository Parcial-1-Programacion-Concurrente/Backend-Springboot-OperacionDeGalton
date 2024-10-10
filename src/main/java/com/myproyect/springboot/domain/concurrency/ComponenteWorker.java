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

    @ManyToOne(optional = false)
    @JoinColumn(name = "componente_id", nullable = false)
    private Componente componente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "maquina_worker_id", nullable = false)
    private MaquinaWorker maquinaWorker;

    @ManyToOne
    @JoinColumn(name = "galton_board_id", nullable = false)
    private GaltonBoard galtonBoard;

    @Transient
    private final Logger logger = Logger.getLogger(ComponenteWorker.class.getName());

    @Getter
    @Transient
    private boolean trabajoCompletado = false;

    @Override
    public void run() {
        try {
            System.out.println("Iniciando trabajo para el ComponenteWorker con tipo: " + componente.getTipo());
            if (galtonBoard == null || galtonBoard.getDistribucion() == null) {
                System.err.println("Error durante el cálculo del valor: El GaltonBoard y su Distribución deben estar configurados.");
                return;
            }

            // Realiza el cálculo del valor.
            double valorCalculado = calcularValor();
            componente.setValorCalculado(valorCalculado);

            // Marca el trabajo como completado.
            trabajoCompletado = true;
            System.out.println("Cálculo de valor completado para el componente de tipo " + componente.getTipo() +
                    " con valor: " + valorCalculado);

        } catch (Exception e) {
            System.err.println("Error durante el cálculo del valor: " + e.getMessage());
            e.printStackTrace();
            Thread.currentThread().interrupt(); // Interrumpe el hilo en caso de error
        }
    }

    private double calcularValor() {
        // Verificación de que el GaltonBoard no es null
        if (galtonBoard == null) {
            throw new IllegalStateException("El GaltonBoard debe estar configurado.");
        }

        // Verificación de que la Distribución no es null
        if (galtonBoard.getDistribucion() == null) {
            throw new IllegalStateException("La Distribución del GaltonBoard no está configurada.");
        }

        var distribucion = galtonBoard.getDistribucion().getDatos();
        if (distribucion == null) {
            throw new IllegalStateException("Los datos de la Distribución no están configurados.");
        }

        // Verifica el número total de bolas
        int totalBolas = galtonBoard.getNumBolas();
        if (totalBolas <= 0) {
            throw new IllegalStateException("El número de bolas en el GaltonBoard debe ser mayor que cero.");
        }

        double valorCalculado = 0.0;
        System.out.println("Distribución del GaltonBoard: " + distribucion); // Verifica la distribución

        // Iterar sobre la distribución para calcular el valor ponderado
        for (var entry : distribucion.entrySet()) {
            String contenedor = entry.getKey();
            int bolasEnContenedor = entry.getValue();

            // Manejar el caso de nombre de contenedor inválido
            try {
                int indiceContenedor = Integer.parseInt(contenedor.replace("Contenedor ", ""));
                System.out.println("Índice del contenedor: " + indiceContenedor + ", Bolas en contenedor: " + bolasEnContenedor);

                // Calcula el valor ponderado basado en el índice del contenedor.
                valorCalculado += indiceContenedor * ((double) bolasEnContenedor / totalBolas);
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear el índice del contenedor: " + contenedor);
            }
        }

        System.out.println("Valor calculado para el componente: " + valorCalculado);
        return valorCalculado;
    }
}


