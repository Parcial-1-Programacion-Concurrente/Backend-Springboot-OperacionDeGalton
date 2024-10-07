package com.myproyect.springboot.domain.factory;

import com.myproyect.springboot.domain.distribution.Componente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "maquinas")
@Getter
@Setter
public class Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "maquina_id", nullable = false)
    private List<Componente> componentes;

    @Column(nullable = false)
    private String estado; // Puede ser 'EN_PRODUCCION', 'ENSAMBLADA', 'EN_ESPERA'
}

