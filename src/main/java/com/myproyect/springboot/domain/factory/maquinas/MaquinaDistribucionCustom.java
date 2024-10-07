package com.myproyect.springboot.domain.factory.maquinas;

import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "maquina_distribucion_custom")
@Getter
@Setter
public class MaquinaDistribucionCustom extends Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "probabilidades_personalizadas", joinColumns = @JoinColumn(name = "maquina_custom_id"))
    @MapKeyColumn(name = "evento")
    @Column(name = "probabilidad")
    private Map<String, Integer> probabilidadesPersonalizadas;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "galton_board_id", nullable = false)
    private GaltonBoard galtonBoard;

    @Column(nullable = false)
    private String estado; // 'EN_SIMULACION', 'FINALIZADA'
}


