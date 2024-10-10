package com.myproyect.springboot.domain.factory.maquinas;

import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "maquina_distribucion_custoom")
@Getter
@Setter
public class MaquinaDistribucionCustom extends Maquina {


    @ElementCollection
    @CollectionTable(name = "probabilidades_personalizadas", joinColumns = @JoinColumn(name = "maquina_custom_id"))
    @MapKeyColumn(name = "evento")
    @Column(name = "probabilidad")
    private Map<String, Integer> probabilidadesPersonalizadas;

    @Column(nullable = false)
    private String estado = "APAGADO"; // 'INICIALIZADO', 'EN_SIMULACION', 'FINALIZADA'
}


