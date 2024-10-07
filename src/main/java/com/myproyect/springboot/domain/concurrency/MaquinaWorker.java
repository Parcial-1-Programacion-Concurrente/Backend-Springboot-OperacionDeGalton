package com.myproyect.springboot.domain.concurrency;

import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maquina_workers")
@Getter
@Setter
public class MaquinaWorker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maquina_id", nullable = false)
    private Maquina maquina;

    @Column(nullable = false)
    private String estado; // 'ACTIVO', 'FINALIZADO'
}
