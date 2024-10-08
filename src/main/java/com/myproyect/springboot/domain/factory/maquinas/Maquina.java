package com.myproyect.springboot.domain.factory.maquinas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.myproyect.springboot.domain.concurrency.Componente;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.Map;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "maquinoas")
@Getter
@Setter
public abstract class Maquina {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column
    private String tipo;

    @Column(nullable = false)
    private int numeroComponentesRequeridos;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "maquina_id", nullable = false)
    private List<Componente> componentes;

    @Transient
    private Map<String, Integer> distribucion; // Mapa para almacenar la distribución de componentes



}


