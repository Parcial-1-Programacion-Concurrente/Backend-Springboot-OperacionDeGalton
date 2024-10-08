package com.myproyect.springboot.domain.concurrency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.concurrent.BlockingQueue;

@Entity
@Table(name = "estacionoes_trabajo")
@Getter
@Setter
public class EstacionTrabajo implements Runnable {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(nullable = false)
    private String nombre;

    @Column
    private String tipo;

    @ManyToOne(fetch = FetchType.EAGER)
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

    public Componente producirComponente() {
        // Crear un nuevo componente según el tipo de estación de trabajo.
        Componente componente = new Componente();
        componente.setTipo(tipo);
        componente.setValorCalculado(0.0); // Valor inicial antes de ser calculado por el ComponenteWorker.
        componente.setEstacionTrabajo(this);

        System.out.println("Componente de tipo " + tipo + " creado por la estación de trabajo: " + nombre);
        return componente;
    }

    /**
     * Producir una cantidad específica de componentes de un tipo dado.
     * @param tipo Tipo de componente a producir.
     * @param cantidad Cantidad de componentes a producir.
     */
    public void producirComponentes(String tipo, int cantidad) {
        System.out.println("Producción de " + cantidad + " componentes de tipo " + tipo + " en la estación de trabajo: " + nombre);

        for (int i = 0; i < cantidad; i++) {
            Componente componente = new Componente();
            componente.setTipo(tipo);
            componente.setValorCalculado(0.0); // Valor inicial antes de ser calculado.
            componente.setEstacionTrabajo(this);

            try {
                bufferComponentes.put(componente);
                System.out.println("Componente de tipo " + tipo + " producido y añadido al buffer por la estación: " + nombre);
            } catch (InterruptedException e) {
                System.err.println("Error al producir el componente: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
}


