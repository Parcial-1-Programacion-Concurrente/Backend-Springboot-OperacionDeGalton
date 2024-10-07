package com.myproyect.springboot.domain.distribution;

import com.myproyect.springboot.domain.concurrency.EstacionTrabajo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "componentes")
@Getter
@Setter
public class Componente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private double valorCalculado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacion_trabajo_id", nullable = false)
    private EstacionTrabajo estacionTrabajo;
}

