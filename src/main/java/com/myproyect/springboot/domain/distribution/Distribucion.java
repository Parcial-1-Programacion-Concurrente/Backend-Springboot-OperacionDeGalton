package com.myproyect.springboot.domain.distribution;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "distribuciones")
@Getter
@Setter
public class Distribucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "distribucion_datos", joinColumns = @JoinColumn(name = "distribucion_id"))
    @MapKeyColumn(name = "tipo_componente")
    @Column(name = "cantidad")
    private Map<String, Integer> datos;

    @Column(nullable = false)
    private int numBolas;

    @Column(nullable = false)
    private int numContenedores;
}

