package com.myproyect.springboot.services;

import com.myproyect.springboot.domain.concurrency.ComponenteWorker;
import com.myproyect.springboot.domain.concurrency.MaquinaWorker;
import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.domain.distribution.Distribucion;
import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionNormal;
import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import com.myproyect.springboot.model.MaquinaWorkerDTO;
import com.myproyect.springboot.repos.*;
import com.myproyect.springboot.repos.maquinasRepos.*;
import com.myproyect.springboot.services.maquinas.*;
import com.myproyect.springboot.util.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class MaquinaWorkerService {

    @Autowired
    private MaquinaDistribucionBinomialService maquinaDistribucionBinomialService;

    @Autowired
    private MaquinaDistribucionGeometricaService maquinaDistribucionGeometricaService;

    @Autowired
    private MaquinaDistribucionExponencialService maquinaDistribucionExponencialService;

    @Autowired
    private MaquinaDistribucionNormalService maquinaDistribucionNormalService;

    @Autowired
    private MaquinaDistribucionUniformeService maquinaDistribucionUniformeService;

    @Autowired
    private MaquinaDistribucionCustomService maquinaDistribucionCustomService;

    @Autowired
    private MaquinaDistribucionPoissonService maquinaDistribucionPoissonService;

    @Autowired
    private DistribucionRepository distribucionRepository;

    @Autowired
    private MaquinaRepository maquinaRepository;

    @Autowired
    private ComponenteRepository componenteRepository;

    @Autowired
    private GaltonBoardRepository galtonBoardRepository;

    private final MaquinaWorkerRepository maquinaWorkerRepository;
    private final ComponenteWorkerRepository componenteWorkerRepository;

    // Mapa concurrente para almacenar MaquinaWorkers por ID de Máquina
    private ConcurrentMap<Integer, MaquinaWorker> maquinaWorkersMap = new ConcurrentHashMap<>();

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

            // Verificar e inicializar la distribución
            if (galtonBoard.getDistribucion() == null) {
                Distribucion distribucion = new Distribucion();
                distribucion.setGaltonBoard(galtonBoard);
                distribucion = distribucionRepository.save(distribucion);
                galtonBoard.setDistribucion(distribucion);
                galtonBoard = galtonBoardRepository.save(galtonBoard);
                System.out.println("Distribución inicializada para el GaltonBoard con ID: " + galtonBoard.getId());
            }

            // Asignar el GaltonBoard a la Maquina antes de guardarla
            maquina.setGaltonBoard(galtonBoard);
            maquina = maquinaRepository.save(maquina);

            if (maquina.getId() == null) {
                System.out.println("Error: La máquina no fue guardada correctamente.");
                return;
            }

            // Crear una nueva instancia de MaquinaWorker
            MaquinaWorker maquinaWorker = new MaquinaWorker();
            maquinaWorker.setMaquina(maquina);
            maquinaWorker.setExecutor(Executors.newFixedThreadPool(maquina.getNumeroComponentesRequeridos()));
            maquinaWorker.setComponenteWorkers(new ArrayList<>());
            maquinaWorker = maquinaWorkerRepository.save(maquinaWorker);

            maquinaWorkersMap.put(maquina.getId(), maquinaWorker);

            Map<String, Integer> distribucion = calcularDistribucion(maquinaWorker);
            galtonBoard.getDistribucion().setDatos(distribucion);
            galtonBoardRepository.save(galtonBoard);

            for (int i = 0; i < maquina.getNumeroComponentesRequeridos(); i++) {
                ComponenteWorker worker = new ComponenteWorker();
                Componente componente = new Componente();
                componente.setTipo("COMPONENTE_TIPO_" + (i + 1));
                componente.setMaquina(maquina);
                componente = componenteRepository.save(componente);

                worker.setComponente(componente);
                worker.setMaquinaWorker(maquinaWorker);
                worker.setGaltonBoard(galtonBoard);
                componenteWorkerRepository.save(worker);
                maquinaWorker.getComponenteWorkers().add(worker);
            }

            maquinaWorkerRepository.save(maquinaWorker);
            new Thread(maquinaWorker).start();
            System.out.println("Trabajo de MaquinaWorker iniciado para la máquina de tipo: " + maquina.getTipo());
        } catch (Exception e) {
            System.err.println("Error al iniciar el trabajo de la máquina de tipo " + maquina.getTipo() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<String, Integer> calcularDistribucion(MaquinaWorker maquinaWorker) {
        Maquina maquina = maquinaWorker.getMaquina();
        Map<String, Integer> distribucion;

        switch (maquina.getTipo().toUpperCase()) {
            case "BINOMIAL":
                distribucion = maquinaDistribucionBinomialService.calcularDistribucion(maquina.getId());
                break;
            case "GEOMETRICA":
                distribucion = maquinaDistribucionGeometricaService.calcularDistribucion(maquina.getId());
                break;
            case "EXPONENCIAL":
                distribucion = maquinaDistribucionExponencialService.calcularDistribucion(maquina.getId());
                break;
            case "NORMAL":
                distribucion = maquinaDistribucionNormalService.calcularDistribucion(maquina.getId());
                break;
            case "UNIFORME":
                distribucion = maquinaDistribucionUniformeService.calcularDistribucion(maquina.getId());
                break;
            case "CUSTOM":
                distribucion = maquinaDistribucionCustomService.calcularDistribucion(maquina.getId());
                break;
            case "POISSON":
                distribucion = maquinaDistribucionPoissonService.calcularDistribucion(maquina.getId());
                break;
            default:
                throw new IllegalArgumentException("Tipo de distribución no soportado: " + maquina.getTipo());
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
        // Obtener el MaquinaWorker desde el mapa concurrente
        return Optional.ofNullable(maquinaWorkersMap.get(maquinaId))
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
