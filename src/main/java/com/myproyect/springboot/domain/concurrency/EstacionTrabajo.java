package com.myproyect.springboot.domain.concurrency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "estaciones_trabajo")
@Getter
@Setter
public class EstacionTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fabrica_gauss_id", nullable = false)
    private FabricaGauss fabricaGauss;

    @OneToMany(mappedBy = "estacionTrabajo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Componente> componentes;

    @Column(nullable = false)
    private int capacidadProduccion;

    @Column(nullable = false)
    private String tipoComponente;
}

