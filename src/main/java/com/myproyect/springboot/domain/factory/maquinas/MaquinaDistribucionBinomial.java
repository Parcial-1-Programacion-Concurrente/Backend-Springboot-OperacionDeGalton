package com.myproyect.springboot.domain.factory.maquinas;

import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maquina_distribucion_binomiaol")
@Getter
@Setter
public class MaquinaDistribucionBinomial extends Maquina {


    @Column(nullable = false)
    private int numEnsayos;

    @Column(nullable = false)
    private double probabilidadExito;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "galton_board_id", nullable = false)
    private GaltonBoard galtonBoard;

    @Column(nullable = false)
    private String estado; // 'EN_SIMULACION', 'FINALIZADA'
}


