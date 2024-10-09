package com.myproyect.springboot.domain.concurrency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Entity
@Table(name = "maquina_workoers")
@Getter
@Setter
public class MaquinaWorker implements Runnable {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "maquinaWorker", cascade = CascadeType.ALL)
    private List<ComponenteWorker> componenteWorkers;

    @ManyToOne
    @JoinColumn(name = "maquina_id", nullable = false)
    private Maquina maquina;

    @Transient
    private ExecutorService executor;

    @Override
    public void run() {
        try {
            System.out.println("Iniciando el ensamblaje de la máquina de tipo: " + maquina.getTipo());

            if (componenteWorkers != null && !componenteWorkers.isEmpty()) {
                // Lanza cada ComponenteWorker usando el executor
                for (ComponenteWorker worker : componenteWorkers) {
                    executor.submit(worker);
                }
                System.out.println("Todos los ComponenteWorkers han sido lanzados para la máquina de tipo: " + maquina.getTipo());
            } else {
                System.out.println("No hay ComponenteWorkers disponibles para la máquina de tipo: " + maquina.getTipo());
            }
        } catch (Exception e) {
            System.err.println("Error durante el ensamblaje de la máquina: " + e.getMessage());
        } finally {
            // Liberar recursos del executor si es necesario
            executor.shutdown();
        }
    }

}

