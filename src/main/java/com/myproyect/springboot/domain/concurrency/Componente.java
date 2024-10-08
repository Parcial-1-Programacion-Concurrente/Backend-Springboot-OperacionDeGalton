package com.myproyect.springboot.domain.concurrency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "componentoes")
@Getter
@Setter
public class Componente {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private double valorCalculado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacion_trabajo_id", nullable = false)
    private EstacionTrabajo estacionTrabajo;
}

