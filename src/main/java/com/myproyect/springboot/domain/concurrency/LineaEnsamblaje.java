package com.myproyect.springboot.domain.concurrency;

import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionNormal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

@Entity
@Table(name = "lineas_ensamblaje")
@Getter
@Setter
public class LineaEnsamblaje implements Runnable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "lineaEnsamblaje", fetch = FetchType.LAZY)
    private FabricaGauss fabricaGauss;

    @Transient
    private BlockingQueue<Componente> bufferCompartido;

    @Column(nullable = false)
    private int capacidadBuffer;

    @Transient
    private Semaphore semaforoComponentes;

    @Transient
    private Maquina maquina;

    private volatile boolean detenerEnsamblaje = false;

    @Override
    public void run() {
        try {
            ensamblarMaquina(maquina);
        } catch (InterruptedException e) {
            System.err.println("El ensamblaje fue interrumpido: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("Proceso de ensamblaje finalizado para la máquina de tipo: " + maquina.getTipo());
        }
    }

    // Metodo que realiza el ensamblaje de una máquina a partir de componentes.
    public void ensamblarMaquina(Maquina maquina) throws InterruptedException {
        System.out.println("Iniciando ensamblaje de una máquina de tipo: " + maquina.getTipo() +
                " en la línea de ensamblaje con capacidad de buffer: " + capacidadBuffer);

        // Iterar hasta que la máquina esté completamente ensamblada o se indique detener.
        while (!detenerEnsamblaje && maquina.getComponentes().size() < maquina.getNumeroComponentesRequeridos()) {
            // Consumir un componente del buffer compartido.
            Componente componente = bufferCompartido.take(); // Espera hasta que haya un componente disponible.
            maquina.getComponentes().add(componente);

            // Simular el tiempo de ensamblaje para cada componente.
            Thread.sleep(1000); // Simular un tiempo de ensamblaje de 1 segundo por componente.

            System.out.println("Componente " + componente.getTipo() + " ensamblado en la máquina de tipo: " + maquina.getTipo());
        }

        if (detenerEnsamblaje) {
            System.out.println("El ensamblaje ha sido detenido antes de completar la máquina de tipo: " + maquina.getTipo());
        } else {
            System.out.println("Máquina de tipo " + maquina.getTipo() + " ensamblada con éxito con " +
                    maquina.getComponentes().size() + " componentes.");
            // En un futuro persistir la máquina ensamblada en la base de datos
            // maquinaRepository.save(maquina);
            notificarFinalizacionEnsamblaje(maquina);
        }
    }

    // Metodo para detener de forma segura el ensamblaje.
    public void detenerEnsamblaje() {
        this.detenerEnsamblaje = true;
    }

    private void notificarFinalizacionEnsamblaje(Maquina maquina) {
        System.out.println("Notificando a la fábrica que la máquina de tipo " + maquina.getTipo() +
                " ha sido ensamblada con éxito.");
    }
}


