package com.myproyect.springboot.domain.synchronization;

import com.myproyect.springboot.domain.distribution.Distribucion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "galton_board")
@Getter
@Setter
public class GaltonBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int numBolas;

    @Column(nullable = false)
    private int numContenedores;

    @OneToOne(mappedBy = "galtonBoard", fetch = FetchType.LAZY)
    private Distribucion distribucion;

    @Column(nullable = false)
    private String estado; // 'EN_SIMULACION', 'FINALIZADA'
}

