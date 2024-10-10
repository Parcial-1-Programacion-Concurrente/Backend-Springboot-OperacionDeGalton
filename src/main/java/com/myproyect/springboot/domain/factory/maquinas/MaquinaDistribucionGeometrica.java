package com.myproyect.springboot.domain.factory.maquinas;

import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maquina_distribucion_geometricoa")
@Getter
@Setter
public class MaquinaDistribucionGeometrica extends Maquina {


    @Column(nullable = false)
    private double probabilidadExito;

    @Column(nullable = false)
    private int maximoEnsayos;

    @Column(nullable = false)
    private String estado = "APAGADO"; // 'INICIALIZADO', 'EN_SIMULACION', 'FINALIZADA'
}


