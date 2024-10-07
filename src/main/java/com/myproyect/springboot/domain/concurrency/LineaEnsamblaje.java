package com.myproyect.springboot.domain.concurrency;

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

    @Override
    public void run() {
        // La lógica de ensamblaje de componentes (se delegará al servicio)
    }
}


