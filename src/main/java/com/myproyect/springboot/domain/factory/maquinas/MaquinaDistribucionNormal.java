package com.myproyect.springboot.domain.factory.maquinas;

import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maquina_distribucion_normoal")
@Getter
@Setter
public class MaquinaDistribucionNormal extends Maquina {

    @Column(nullable = false)
    private double media;

    @Column(nullable = false)
    private double desviacionEstandar;

    @Column(nullable = false)
    private int maximoValor;

    @Column(nullable = false)
    private String estado = "APAGADO"; // 'INICIALIZADO', 'EN_SIMULACION', 'FINALIZADA'

}

