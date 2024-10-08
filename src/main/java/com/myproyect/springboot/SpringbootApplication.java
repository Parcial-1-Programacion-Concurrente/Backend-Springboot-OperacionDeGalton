package com.myproyect.springboot;

import com.myproyect.springboot.domain.concurrency.FabricaGauss;
import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import com.myproyect.springboot.domain.concurrency.LineaEnsamblaje;
import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionNormal;
import com.myproyect.springboot.services.FabricaGaussService;
import com.myproyect.springboot.services.GaltonBoardService;
import com.myproyect.springboot.services.LineaEnsamblajeService;
import com.myproyect.springboot.services.MaquinaWorkerService;
import com.myproyect.springboot.model.FabricaGaussDTO;
import com.myproyect.springboot.model.GaltonBoardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class SpringbootApplication implements CommandLineRunner {

    @Autowired
    private FabricaGaussService fabricaGaussService;

    @Autowired
    private LineaEnsamblajeService lineaEnsamblajeService;

    @Autowired
    private GaltonBoardService galtonBoardService;

    @Autowired
    private MaquinaWorkerService maquinaWorkerService;

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Iniciando simulación de la fábrica de Gauss...");

        // Crear una fábrica y asignar estaciones de trabajo y línea de ensamblaje.
        FabricaGauss fabrica = new FabricaGauss();
        fabrica.setNombre("Fábrica de Campanas de Gauss");
        Integer fabricaId = fabricaGaussService.create(fabricaGaussService.mapToDTO(fabrica, new FabricaGaussDTO()));

        // Configurar la línea de ensamblaje.
        LineaEnsamblaje lineaEnsamblaje = new LineaEnsamblaje();
        lineaEnsamblaje.setCapacidadBuffer(10);
        fabrica.setLineaEnsamblaje(lineaEnsamblaje);

        // Crear y simular un GaltonBoard.
        GaltonBoard galtonBoard = new GaltonBoard();
        galtonBoard.setNumBolas(1000);
        galtonBoard.setNumContenedores(10);
        galtonBoard.setEstado("EN_SIMULACION");
        Integer galtonBoardId = galtonBoardService.create(galtonBoardService.mapToDTO(galtonBoard, new GaltonBoardDTO()));
        galtonBoardService.simularCaidaDeBolas(galtonBoardId);

        // Asignar tareas a las estaciones de trabajo y producir componentes.
        fabricaGaussService.asignarTareas(fabricaId);

        // Iniciar el ensamblaje de la máquina con la línea de ensamblaje.
        MaquinaDistribucionNormal maquina = new MaquinaDistribucionNormal();
        lineaEnsamblajeService.iniciarEnsamblaje(lineaEnsamblaje.getId(), maquina);

        // Iniciar el trabajo de la máquina y calcular la distribución.
        maquinaWorkerService.iniciarTrabajo(maquina);

        // Mostrar la distribución final.
        galtonBoardService.mostrarDistribucion(galtonBoardId);

        System.out.println("Simulación de la fábrica de Gauss finalizada.");
    }
}


