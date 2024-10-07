package com.myproyect.springboot.domain.concurrency;

import com.myproyect.springboot.domain.distribution.Componente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "componente_workers")
@Getter
@Setter
public class ComponenteWorker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "componente_id", nullable = false)
    private Componente componente;

    @Column(nullable = false)
    private String estado; // 'EN_CALCULO', 'FINALIZADO'
}

