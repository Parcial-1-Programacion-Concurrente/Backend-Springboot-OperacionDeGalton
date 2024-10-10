package com.myproyect.springboot.services;

import com.myproyect.springboot.domain.concurrency.ComponenteWorker;
import com.myproyect.springboot.domain.concurrency.MaquinaWorker;
import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import com.myproyect.springboot.model.MaquinaWorkerDTO;
import com.myproyect.springboot.repos.ComponenteRepository;
import com.myproyect.springboot.repos.ComponenteWorkerRepository;
import com.myproyect.springboot.repos.GaltonBoardRepository;
import com.myproyect.springboot.repos.MaquinaWorkerRepository;
import com.myproyect.springboot.repos.maquinasRepos.MaquinaRepository;
import com.myproyect.springboot.util.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class MaquinaWorkerService {

    @Autowired
    private MaquinaRepository maquinaRepository;

    @Autowired
    private ComponenteRepository componenteRepository;

    @Autowired
    private GaltonBoardRepository galtonBoardRepository;

    private final MaquinaWorkerRepository maquinaWorkerRepository;
    private final ComponenteWorkerRepository componenteWorkerRepository;

    @Autowired
    public MaquinaWorkerService(final MaquinaWorkerRepository maquinaWorkerRepository,
                                final ComponenteWorkerRepository componenteWorkerRepository) {
        this.maquinaWorkerRepository = maquinaWorkerRepository;
        this.componenteWorkerRepository = componenteWorkerRepository;
    }

    @Transactional
    public void iniciarTrabajo(Maquina maquina, GaltonBoard galtonBoard) {
        try {

            // Verificar si el GaltonBoard está persistido
            if (galtonBoard.getId() == null) {
                galtonBoard = galtonBoardRepository.save(galtonBoard);
                System.out.println("GaltonBoard guardado con ID: " + galtonBoard.getId());
            } else {
                System.out.println("GaltonBoard ya está persistido con ID: " + galtonBoard.getId());
            }

            // Asignar el GaltonBoard a la Maquina antes de guardarla
            maquina.setGaltonBoard(galtonBoard);

            // Guardar la máquina antes de asociarla a un MaquinaWorker
            maquina = maquinaRepository.save(maquina);

            if (maquina.getId() == null) {
                System.out.println("Error: La máquina no fue guardada correctamente.");
                return;
            }


            // Crear una nueva instancia de MaquinaWorker
            MaquinaWorker maquinaWorker = new MaquinaWorker();
            maquinaWorker.setMaquina(maquina);

            int numComponentes = maquina.getNumeroComponentesRequeridos();

            if (numComponentes <= 0) {
                throw new IllegalArgumentException("El número de componentes requeridos debe ser mayor que cero. Valor actual: " + numComponentes);
            }

            System.out.println("Número de componentes requeridos para la máquina de tipo " + maquina.getTipo() + ": " + numComponentes);

            maquinaWorker.setExecutor(Executors.newFixedThreadPool(numComponentes));

            // Inicializar la lista de ComponenteWorkers
            maquinaWorker.setComponenteWorkers(new ArrayList<>());

            // Guardar el MaquinaWorker antes de asignarle los ComponenteWorkers
            maquinaWorker = maquinaWorkerRepository.save(maquinaWorker);


            // Crear y asignar los ComponenteWorkers
            for (int i = 0; i < numComponentes; i++) {
                ComponenteWorker worker = new ComponenteWorker();

                // Crear un nuevo componente para cada worker
                Componente componente = new Componente();
                componente.setTipo("COMPONENTE_TIPO_" + (i + 1)); // Asignar un tipo único

                // Asignar la máquina al componente
                componente.setMaquina(maquina);

                // Guardar el componente antes de asociarlo
                componente = componenteRepository.save(componente);

                // Asigna el componente al worker
                worker.setComponente(componente);

                // Asignar la máquina worker
                worker.setMaquinaWorker(maquinaWorker);

                worker.setGaltonBoard(galtonBoard);

                // Agregar el worker a la lista de componentes
                maquinaWorker.getComponenteWorkers().add(worker);
            }

            // Guardar el MaquinaWorker en la base de datos
            maquinaWorker = maquinaWorkerRepository.save(maquinaWorker);

            // Ejecutar el MaquinaWorker en un nuevo hilo
            new Thread(maquinaWorker).start();

            System.out.println("Trabajo de MaquinaWorker iniciado para la máquina de tipo: " + maquina.getTipo());
        } catch (Exception e) {
            System.out.println("Error al iniciar el trabajo de la máquina de tipo " + maquina.getTipo() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Metodo para calcular la distribución de los componentes de la máquina
    public Map<String, Integer> calcularDistribucion(MaquinaWorker maquinaWorker) {
        Maquina maquina = maquinaWorker.getMaquina();
        Map<String, Integer> distribucion = new HashMap<>();

        // Simulación del cálculo de distribución basado en los valores de los componentes.
        if (maquina.getComponentes() != null) {
            maquina.getComponentes().forEach(componente -> {
                String tipo = componente.getTipo();
                distribucion.put(tipo, distribucion.getOrDefault(tipo, 0) + 1);
            });
        }

        System.out.println("Distribución calculada para la máquina de tipo " + maquina.getTipo() + ": " + distribucion);
        return distribucion;
    }

    // Metodo para ensamblar la máquina una vez que todos los ComponenteWorkers han terminado su trabajo
    public void ensamblarMaquina(MaquinaWorker maquinaWorker) {
        // Verificar si todos los ComponenteWorkers han terminado su trabajo
        List<ComponenteWorker> workersPendientes = componenteWorkerRepository.findAllByMaquinaWorker(maquinaWorker)
                .stream().filter(worker -> !worker.isTrabajoCompletado()).collect(Collectors.toList());

        if (workersPendientes.isEmpty() && validarComponentes(maquinaWorker.getMaquina())) {
            // Calcular la distribución final de los componentes de la máquina
            Map<String, Integer> distribucionFinal = calcularDistribucion(maquinaWorker);
            maquinaWorker.getMaquina().setDistribucion(distribucionFinal);
            maquinaWorkerRepository.save(maquinaWorker);

            System.out.println("La máquina de tipo " + maquinaWorker.getMaquina().getTipo() +
                    " ha sido ensamblada y guardada con la distribución final: " + distribucionFinal);
        } else {
            System.out.println("Aún hay ComponenteWorkers pendientes de completar su trabajo para la máquina de tipo " +
                    maquinaWorker.getMaquina().getTipo());
        }
    }

    public List<MaquinaWorkerDTO> findAll() {
        return maquinaWorkerRepository.findAll(Sort.by("id")).stream()
                .map(worker -> mapToDTO(worker, new MaquinaWorkerDTO()))
                .collect(Collectors.toList());
    }

    public MaquinaWorkerDTO get(final Integer id) {
        return maquinaWorkerRepository.findById(id)
                .map(worker -> mapToDTO(worker, new MaquinaWorkerDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final MaquinaWorkerDTO maquinaWorkerDTO) {
        MaquinaWorker maquinaWorker = new MaquinaWorker();
        mapToEntity(maquinaWorkerDTO, maquinaWorker);
        return maquinaWorkerRepository.save(maquinaWorker).getId();
    }

    public void update(final Integer id, final MaquinaWorkerDTO maquinaWorkerDTO) {
        MaquinaWorker maquinaWorker = maquinaWorkerRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(maquinaWorkerDTO, maquinaWorker);
        maquinaWorkerRepository.save(maquinaWorker);
    }

    public void delete(final Integer id) {
        maquinaWorkerRepository.deleteById(id);
    }

    private MaquinaWorkerDTO mapToDTO(final MaquinaWorker worker, final MaquinaWorkerDTO dto) {
        dto.setId(worker.getId());
        dto.setMaquinaId(worker.getMaquina().getId());
        return dto;
    }

    private MaquinaWorker mapToEntity(final MaquinaWorkerDTO dto, final MaquinaWorker worker) {
        if (dto.getMaquinaId() != null) {
            // Buscar la instancia de Maquina en el repositorio y asociarla con el worker.
            Maquina maquina = maquinaRepository.findById(dto.getMaquinaId())
                    .orElseThrow(() -> new NotFoundException("Maquina no encontrada con ID: " + dto.getMaquinaId()));
            worker.setMaquina(maquina);
        }
        return worker;
    }

    public MaquinaWorker obtenerMaquinaWorker(Integer maquinaId) {
        return maquinaWorkerRepository.findById(maquinaId)
                .orElseThrow(() -> new NotFoundException("No se encontró MaquinaWorker para la máquina ID: " + maquinaId));
    }

    private boolean validarComponentes(Maquina maquina) {
        // Verificar que la lista de componentes no sea nula ni esté vacía.
        if (maquina.getComponentes() == null || maquina.getComponentes().isEmpty()) {
            System.out.println("La máquina no tiene componentes asignados.");
            return false;
        }

        // Verificar que la máquina tenga al menos un número mínimo de componentes (por ejemplo, 3).
        int minimoComponentes = 3;
        if (maquina.getNumeroComponentesRequeridos() < minimoComponentes) {
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
}
