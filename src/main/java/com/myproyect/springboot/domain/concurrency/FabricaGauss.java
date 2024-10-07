package com.myproyect.springboot.domain.concurrency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "FabricaGauss")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class FabricaGauss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "fabricaGauss", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EstacionTrabajo> estaciones;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "linea_ensamblaje_id", nullable = false)
    private LineaEnsamblaje lineaEnsamblaje;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;
}

