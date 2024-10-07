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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fabrica_gauss_id", nullable = false)
    private FabricaGauss fabricaGauss;

    @Transient
    private BlockingQueue<Componente> bufferComponentes;

    @Column(nullable = false)
    private int capacidadBuffer;

    @Override
    public void run() {
        // La lógica de producción de componentes (se delegará al servicio)
    }
}


