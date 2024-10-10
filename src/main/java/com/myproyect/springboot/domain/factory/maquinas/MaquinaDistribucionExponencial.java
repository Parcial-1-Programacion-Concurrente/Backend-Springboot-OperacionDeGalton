package com.myproyect.springboot.domain.factory.maquinas;

import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maquina_distribucion_exponencioal")
@Getter
@Setter
public class MaquinaDistribucionExponencial extends Maquina {


    @Column(nullable = false)
    private double lambda;

    @Column(nullable = false)
    private int maximoValor;

    @Column(nullable = false)
    private String estado = "APAGADO"; // 'INICIALIZADO', 'EN_SIMULACION', 'FINALIZADA'
}

