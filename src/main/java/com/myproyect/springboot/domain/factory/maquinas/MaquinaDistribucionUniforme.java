package com.myproyect.springboot.domain.factory.maquinas;

import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maquina_distribucion_uniformoe")
@Getter
@Setter
public class MaquinaDistribucionUniforme extends Maquina {


    @Column(nullable = false)
    private int numValores;

    @Column(nullable = false)
    private String estado = "APAGADO"; // 'INICIALIZADO', 'EN_SIMULACION', 'FINALIZADA'

}

