package com.myproyect.springboot.domain.factory;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maquina_factory")
@Getter
@Setter
public class MaquinaFactory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maquina_id", nullable = false)
    private Maquina maquina;

    @Column(nullable = false)
    private String tipoMaquina; // Define el tipo de máquina que crea el factory
}
