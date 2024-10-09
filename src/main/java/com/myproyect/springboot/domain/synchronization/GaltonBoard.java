package com.myproyect.springboot.domain.synchronization;

import com.myproyect.springboot.domain.distribution.Distribucion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "galton_boarod")
@Getter
@Setter
public class GaltonBoard implements Runnable {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private int numBolas;

    @Column(nullable = false)
    private int numContenedores;

    @OneToOne(mappedBy = "galtonBoard", cascade = CascadeType.ALL)
    private Distribucion distribucion;

    @Column(nullable = false)
    private String estado = "APAGADO"; // 'EN_SIMULACION', 'FINALIZADA'

    @Override
    public void run() {
        // Delegar la simulación de la caída de bolas al servicio.
        try {
            // Simular la caida de bolas aquí o llamar a un metodo de servicio para hacerlo
            System.out.println("Iniciando la simulación de caída de bolas en el GaltonBoard.");
        } catch (Exception e) {
            System.err.println("Error durante la simulación de caída de bolas: " + e.getMessage());
        }
    }
}

