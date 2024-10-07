package com.myproyect.springboot.domain.concurrency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.concurrent.BlockingQueue;

@Entity
@Table(name = "estaciones_trabajo")
@Getter
@Setter
public class EstacionTrabajo implements Runnable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fabrica_gauss_id", nullable = false)
    private FabricaGauss fabricaGauss;

    @Transient
    private BlockingQueue<Componente> bufferComponentes;

    @Column(nullable = false)
    private int capacidadBuffer;

    @Override
    public void run() {
        try {
            while (true) {
                // Producir un nuevo componente y colocarlo en el buffer.
                Componente componente = producirComponente();

                // Bloquear si el buffer está lleno, hasta que haya espacio disponible.
                bufferComponentes.put(componente);

                System.out.println("Estación de trabajo " + nombre + " ha producido un componente de tipo " + componente.getTipo());

                // Simular el tiempo de producción.
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.err.println("Producción interrumpida en la estación de trabajo: " + nombre);
            Thread.currentThread().interrupt();
        }
    }

    private Componente producirComponente() {
        // Crear un nuevo componente según el tipo de estación de trabajo.
        Componente componente = new Componente();
        componente.setTipo(tipo);
        componente.setValorCalculado(0.0); // Valor inicial antes de ser calculado por el ComponenteWorker.
        componente.setEstacionTrabajo(this);

        System.out.println("Componente de tipo " + tipo + " creado por la estación de trabajo: " + nombre);
        return componente;
    }
}


