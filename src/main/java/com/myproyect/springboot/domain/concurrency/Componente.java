package com.myproyect.springboot.domain.concurrency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

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

    @OneToMany(mappedBy = "componente")
    private List<ComponenteWorker> componenteWorkers;

    @ManyToOne(optional = false)
    @JoinColumn(name = "maquina_id", nullable = false)
    private Maquina maquina;
}

