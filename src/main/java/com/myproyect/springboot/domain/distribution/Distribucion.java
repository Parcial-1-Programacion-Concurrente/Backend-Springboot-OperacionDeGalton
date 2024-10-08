package com.myproyect.springboot.domain.distribution;

import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Map;

@Entity
@Table(name = "distribucionoes")
@Getter
@Setter
public class Distribucion {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ElementCollection
    @CollectionTable(name = "distribucion_datos", joinColumns = @JoinColumn(name = "distribucion_id"))
    @MapKeyColumn(name = "tipo_componente")
    @Column(name = "cantidad")
    private Map<String, Integer> datos;

    @Column(nullable = false)
    private int numBolas;

    @Column(nullable = false)
    private int numContenedores;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "galton_board_id", nullable = false)
    private GaltonBoard galtonBoard;
}

