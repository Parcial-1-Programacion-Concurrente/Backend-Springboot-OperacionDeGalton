package com.myproyect.springboot.services;

import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.domain.concurrency.FabricaGauss;
import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import com.myproyect.springboot.domain.concurrency.EstacionTrabajo;
import com.myproyect.springboot.domain.concurrency.LineaEnsamblaje;
import com.myproyect.springboot.model.FabricaGaussDTO;
import com.myproyect.springboot.repos.FabricaGaussRepository;
import com.myproyect.springboot.repos.MaquinaRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class FabricaGaussService {

    private final FabricaGaussRepository fabricaGaussRepository;
    private final MaquinaRepository maquinaRepository;
    private final ExecutorService executorService;

    @Autowired
    public FabricaGaussService(final FabricaGaussRepository fabricaGaussRepository,
                               final MaquinaRepository maquinaRepository) {
        this.fabricaGaussRepository = fabricaGaussRepository;
        this.maquinaRepository = maquinaRepository;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    // Metodo para iniciar la producción y ensamblaje de diferentes máquinas.
    public void iniciarProduccion(Long fabricaId) {
        FabricaGauss fabrica = fabricaGaussRepository.findById(fabricaId)
                .orElseThrow(() -> new NotFoundException("Fábrica no encontrada con ID: " + fabricaId));

        System.out.println("Iniciando la producción en la fábrica: " + fabrica.getNombre());

        // Iniciar la producción en cada estación de trabajo de manera paralela.
        for (EstacionTrabajo estacion : fabrica.getEstaciones()) {
            executorService.submit(() -> {
                try {
                    estacion.producirComponente(); // Simular la producción de componentes.
                    System.out.println("Producción completada en estación de trabajo: " + estacion.getNombre());
                } catch (Exception e) {
                    System.err.println("Error en la estación de trabajo " + estacion.getNombre() + ": " + e.getMessage());
                }
            });
        }

        // Iniciar la línea de ensamblaje de manera paralela.
        executorService.submit(() -> {
            try {
                fabrica.getLineaEnsamblaje().run(); // Ejecutar la lógica de la línea de ensamblaje.
                System.out.println("Ensamblaje completado en la línea de ensamblaje de la fábrica: " + fabrica.getNombre());
            } catch (Exception e) {
                System.err.println("Error en la línea de ensamblaje de la fábrica " + fabrica.getNombre() + ": " + e.getMessage());
            }
        });
    }

    // Metodo para detener la producción en curso.
    public void detenerProduccion() {
        System.out.println("Deteniendo la producción en la fábrica.");
        executorService.shutdownNow(); // Interrumpe todas las tareas en ejecución.
    }

    // Metodo para asignar tareas a las estaciones de trabajo.
    public void asignarTareas(Long fabricaId) {
        // Obtener la fábrica por su ID.
        FabricaGauss fabrica = fabricaGaussRepository.findById(fabricaId)
                .orElseThrow(() -> new NotFoundException("Fábrica no encontrada con ID: " + fabricaId));

        System.out.println("Asignando tareas a las estaciones de trabajo de la fábrica: " + fabrica.getNombre());

        // Asignar y lanzar tareas específicas a cada estación.
        fabrica.getEstaciones().forEach(estacion -> {
            try {
                // Asignar tareas de producción específicas según el tipo de estación.
                if (estacion.getTipo().equalsIgnoreCase("TipoA")) {
                    // Supongamos que el tipo A produce componentes de tipo X.
                    estacion.producirComponentes("COMPONENTE_TIPO_X", 100);
                } else if (estacion.getTipo().equalsIgnoreCase("TipoB")) {
                    // Supongamos que el tipo B produce componentes de tipo Y.
                    estacion.producirComponentes("COMPONENTE_TIPO_Y", 200);
                } else if (estacion.getTipo().equalsIgnoreCase("TipoC")) {
                    // Supongamos que el tipo C produce componentes de tipo Z.
                    estacion.producirComponentes("COMPONENTE_TIPO_Z", 150);
                } else {
                    // Si el tipo de estación no es reconocido, lanzar una excepción.
                    throw new IllegalArgumentException("Tipo de estación desconocido: " + estacion.getTipo());
                }
                System.out.println("Tarea asignada a la estación de trabajo: " + estacion.getNombre()
                        + " para producir componentes de tipo: " + estacion.getTipo());

            } catch (Exception e) {
                System.err.println("Error al asignar tarea a la estación " + estacion.getNombre()
                        + ": " + e.getMessage());
            }
        });
    }


    // Metodo para ensamblar una máquina específica.
    public void ensamblarMaquina(Maquina maquina) {
        // Validar y ensamblar la máquina, asegurando que los componentes sean adecuados.
        if (validarComponentes(maquina)) {
            maquinaRepository.save(maquina);
            System.out.println("Máquina de tipo " + maquina.getTipo() + " ensamblada y guardada con éxito.");
        } else {
            System.err.println("La máquina de tipo " + maquina.getTipo() + " no pasó la validación de componentes.");
        }
    }

    // Metodo privado para validar los componentes de una máquina antes del ensamblaje.
    private boolean validarComponentes(Maquina maquina) {
        // Verificar que la lista de componentes no sea nula ni esté vacía.
        if (maquina.getComponentes() == null || maquina.getComponentes().isEmpty()) {
            System.out.println("La máquina no tiene componentes asignados.");
            return false;
        }

        // Verificar que la máquina tenga al menos un número mínimo de componentes (por ejemplo, 3).
        int minimoComponentes = 3;
        if (maquina.getComponentes().size() < minimoComponentes) {
            System.out.println("La máquina requiere al menos " + minimoComponentes + " componentes para ser ensamblada.");
            return false;
        }

        // Validar que los componentes sean de tipos válidos según el tipo de máquina.
        for (Componente componente : maquina.getComponentes()) {
            if (!esTipoComponenteValido(componente, maquina.getTipo())) {
                System.out.println("Componente de tipo " + componente.getTipo() + " no es válido para la máquina de tipo " + maquina.getTipo());
                return false;
            }

            // Verificar que el valor calculado del componente esté en un rango válido (por ejemplo, entre 0 y 100).
            if (componente.getValorCalculado() < 0 || componente.getValorCalculado() > 100) {
                System.out.println("El componente " + componente.getTipo() + " tiene un valor calculado fuera del rango permitido: " + componente.getValorCalculado());
                return false;
            }
        }

        // Si todas las validaciones se superan, la máquina es considerada válida.
        System.out.println("La máquina de tipo " + maquina.getTipo() + " ha pasado todas las validaciones de componentes.");
        return true;
    }

    // Metodo auxiliar para validar si un componente es adecuado para un tipo de máquina específico.
    private boolean esTipoComponenteValido(Componente componente, String tipoMaquina) {
        // Dependiendo del tipo de la máquina, se validan los tipos de componentes permitidos.
        switch (tipoMaquina.toUpperCase()) {
            case "BINOMIAL":
                return "COMPONENTE_BINOMIAL".equalsIgnoreCase(componente.getTipo());
            case "GEOMETRICA":
                return "COMPONENTE_GEOMETRICA".equalsIgnoreCase(componente.getTipo());
            case "EXPONENCIAL":
                return "COMPONENTE_EXPONENCIAL".equalsIgnoreCase(componente.getTipo());
            case "NORMAL":
                return "COMPONENTE_NORMAL".equalsIgnoreCase(componente.getTipo());
            case "UNIFORME":
                return "COMPONENTE_UNIFORME".equalsIgnoreCase(componente.getTipo());
            case "CUSTOM":
                return "COMPONENTE_CUSTOM".equalsIgnoreCase(componente.getTipo());
            case "POISSON":
                return "COMPONENTE_POISSON".equalsIgnoreCase(componente.getTipo());
            default:
                System.out.println("Tipo de máquina desconocido: " + tipoMaquina);
                return false;
        }
    }


    // Métodos CRUD para la gestión de la fábrica.
    public List<FabricaGaussDTO> findAll() {
        return fabricaGaussRepository.findAll(Sort.by("id")).stream()
                .map(fabrica -> mapToDTO(fabrica, new FabricaGaussDTO()))
                .collect(Collectors.toList());
    }

    public FabricaGaussDTO get(final Long id) {
        return fabricaGaussRepository.findById(id)
                .map(fabrica -> mapToDTO(fabrica, new FabricaGaussDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final FabricaGaussDTO fabricaGaussDTO) {
        FabricaGauss fabricaGauss = new FabricaGauss();
        mapToEntity(fabricaGaussDTO, fabricaGauss);
        return fabricaGaussRepository.save(fabricaGauss).getId();
    }

    public void update(final Long id, final FabricaGaussDTO fabricaGaussDTO) {
        FabricaGauss fabricaGauss = fabricaGaussRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(fabricaGaussDTO, fabricaGauss);
        fabricaGaussRepository.save(fabricaGauss);
    }

    public void delete(final Long id) {
        fabricaGaussRepository.deleteById(id);
    }

    // Métodos de mapeo entre la entidad y el DTO.
    private FabricaGaussDTO mapToDTO(final FabricaGauss fabricaGauss, final FabricaGaussDTO fabricaGaussDTO) {
        fabricaGaussDTO.setId(fabricaGauss.getId());
        fabricaGaussDTO.setNombre(fabricaGauss.getNombre());
        fabricaGaussDTO.setDateCreated(fabricaGauss.getDateCreated());
        return fabricaGaussDTO;
    }

    private FabricaGauss mapToEntity(final FabricaGaussDTO fabricaGaussDTO, final FabricaGauss fabricaGauss) {
        fabricaGauss.setNombre(fabricaGaussDTO.getNombre());
        return fabricaGauss;
    }
}

