package com.myproyect.springboot.domain.concurrency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lineas_ensamblaje")
@Getter
@Setter
public class LineaEnsamblaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "lineaEnsamblaje", fetch = FetchType.LAZY)
    private FabricaGauss fabricaGauss;

    @Column(nullable = false)
    private int capacidadBuffer;

    @Column(nullable = false)
    private int tiempoEnsamblaje; // Tiempo promedio para ensamblar una máquina (en segundos)
}

