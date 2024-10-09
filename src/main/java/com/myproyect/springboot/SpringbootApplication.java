package com.myproyect.springboot;

import com.myproyect.springboot.domain.concurrency.MaquinaWorker;
import com.myproyect.springboot.domain.factory.maquinas.FabricaGauss;
import com.myproyect.springboot.model.FabricaGaussDTO;
import com.myproyect.springboot.services.FabricaGaussService;
import jakarta.annotation.PostConstruct;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.Scanner;

@SpringBootApplication
public class SpringbootApplication implements CommandLineRunner {

    @Autowired
    private FabricaGaussService fabricaGaussService;

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Iniciando simulación de la fábrica de Gauss...");

            // Paso 1: Crear la fábrica de Gauss
            FabricaGaussDTO fabricaGaussDTO = new FabricaGaussDTO();
            fabricaGaussDTO.setNombre("Fábrica de Campanas de Gauss");
            Integer fabricaId = fabricaGaussService.create(fabricaGaussDTO);
            System.out.println("Fábrica creada con ID: " + fabricaId);

            // Paso 2: Iniciar la producción de máquinas y distribuciones
            System.out.println("Iniciando la producción de máquinas...");
            new Thread(fabricaGaussService::iniciarProduccion).start();

            // Paso 3: Monitorear para detener la simulación
            System.out.println("Escribe 'detener' y presiona Enter para detener la simulación:");
            while (true) {
                String input = scanner.nextLine();
                if ("detener".equalsIgnoreCase(input.trim())) {
                    fabricaGaussService.detenerSimulacion();
                    System.out.println("La detención de la simulación ha sido solicitada.");
                    break;
                }
            }

            // Esperar un poco para que los hilos puedan terminar sus tareas actuales
            Thread.sleep(2000);

            System.out.println("Simulación completa.");
        } catch (Exception e) {
            System.err.println("Ocurrió un error durante la simulación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}