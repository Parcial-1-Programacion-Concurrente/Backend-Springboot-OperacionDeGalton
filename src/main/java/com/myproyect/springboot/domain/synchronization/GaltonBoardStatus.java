package com.myproyect.springboot.domain.synchronization;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "galton_board_status")
@Getter
@Setter
public class GaltonBoardStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String estado;

    @ElementCollection
    @CollectionTable(name = "distribucion_actual", joinColumns = @JoinColumn(name = "status_id"))
    @MapKeyColumn(name = "contenedor")
    @Column(name = "bolas")
    private Map<String, Integer> distribucionActual = new HashMap<>();


    public GaltonBoardStatus(Integer id, String estado, Map<String, Integer> distribucionActual) {
        this.id = id;
        this.estado = estado;
        this.distribucionActual = distribucionActual;
    }

    public GaltonBoardStatus() {
    }
}

