package com.myproyect.springboot.domain.factory;

import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "maquinoa_factory")
@Getter
@Setter
public class MaquinaFactory {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maquina_id", nullable = false)
    private Maquina maquina;

    @Column(nullable = false)
    private String tipoMaquina; // Define el tipo de máquina que crea el factory
}
