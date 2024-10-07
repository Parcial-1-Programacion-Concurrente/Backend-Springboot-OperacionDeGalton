package com.myproyect.springboot.domain.concurrency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Entity
@Table(name = "maquina_workers")
@Getter
@Setter
public class MaquinaWorker implements Runnable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maquina_id", nullable = false)
    private Maquina maquina;

    @Transient
    private List<ComponenteWorker> componenteWorkers;

    @Transient
    private ExecutorService executor;

    @Override
    public void run() {
        // La lógica de ensamblaje y distribución de la máquina (se delegará al servicio)
    }
}

