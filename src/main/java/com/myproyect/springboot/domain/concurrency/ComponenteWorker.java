package com.myproyect.springboot.domain.concurrency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.domain.synchronization.GaltonBoard;

@Entity
@Table(name = "componente_workers")
@Getter
@Setter
public class ComponenteWorker implements Runnable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "componente_id", nullable = false)
    private Componente componente;

    @Transient
    private GaltonBoard galtonBoard;

    @Override
    public void run() {
        // La lógica para calcular el valor del componente usando el tablero de Galton (se delegará al servicio)
    }
}


