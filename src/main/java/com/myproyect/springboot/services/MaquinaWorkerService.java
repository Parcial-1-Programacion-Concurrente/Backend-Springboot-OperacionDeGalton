package com.myproyect.springboot.services;

import com.myproyect.springboot.domain.concurrency.ComponenteWorker;
import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import com.myproyect.springboot.model.MaquinaWorkerDTO;
import com.myproyect.springboot.domain.concurrency.MaquinaWorker;
import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.repos.ComponenteWorkerRepository;
import com.myproyect.springboot.repos.maquinasRepos.MaquinaRepository;
import com.myproyect.springboot.repos.MaquinaWorkerRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class MaquinaWorkerService {

    @Autowired
    private MaquinaRepository maquinaRepository;


    private final MaquinaWorkerRepository maquinaWorkerRepository;
    private final ComponenteWorkerRepository componenteWorkerRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Autowired
    public MaquinaWorkerService(final MaquinaWorkerRepository maquinaWorkerRepository,
                                final ComponenteWorkerRepository componenteWorkerRepository) {
        this.maquinaWorkerRepository = maquinaWorkerRepository;
        this.componenteWorkerRepository = componenteWorkerRepository;
    }

    public void iniciarTrabajo(Maquina maquina, GaltonBoard galtonBoard) {
        // Guardar la máquina antes de asociarla a un MaquinaWorker
        maquina = maquinaRepository.save(maquina);

        // Crear una nueva instancia de MaquinaWorker
        MaquinaWorker maquinaWorker = new MaquinaWorker();
        maquinaWorker.setMaquina(maquina);
        maquinaWorker.setExecutor(Executors.newFixedThreadPool(maquina.getNumeroComponentesRequeridos()));

        // Inicializar la lista de ComponenteWorkers si no está ya inicializada
        if (maquinaWorker.getComponenteWorkers() == null) {
            maquinaWorker.setComponenteWorkers(new ArrayList<>());
        }

        // Dentro de iniciarTrabajo en MaquinaWorkerService
        for (int i = 0; i < maquina.getNumeroComponentesRequeridos(); i++) {
            ComponenteWorker worker = new ComponenteWorker();

            // Crea un nuevo componente para cada worker
            Componente componente = new Componente();
            componente.setTipo("COMPONENTE_TIPO_" + (i + 1)); // Asignar un tipo único
            worker.setComponente(componente); // Asigna el componente al worker

            // Asignar la máquina
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
    }


    // Metodo para calcular la distribución de los componentes de la máquina
    public Map<String, Integer> calcularDistribucion(MaquinaWorker maquinaWorker) {
        Maquina maquina = maquinaWorker.getMaquina();
        Map<String, Integer> distribucion = new HashMap<>();

        // Simulación del cálculo de distribución basado en los valores de los componentes.
        maquina.getComponentes().forEach(componente -> {
            String tipo = componente.getTipo();
            distribucion.put(tipo, distribucion.getOrDefault(tipo, 0) + 1);
        });

        System.out.println("Distribución calculada para la máquina de tipo " + maquina.getTipo() + ": " + distribucion);
        return distribucion;
    }

    // Metodo para agregar un ComponenteWorker y ejecutarlo
    public void agregarComponenteWorker(ComponenteWorker worker) {
        // Persistir el ComponenteWorker antes de lanzarlo.
        componenteWorkerRepository.save(worker);
        // Añadir el ComponenteWorker al ExecutorService para que sea ejecutado.
        executorService.submit(worker);
        System.out.println("ComponenteWorker agregado para el componente de tipo " + worker.getComponente().getTipo());
    }

    // Metodo para ensamblar la máquina una vez que todos los ComponenteWorkers han terminado su trabajo
    public void ensamblarMaquina(MaquinaWorker maquinaWorker) {
        // Verificar si todos los ComponenteWorkers han terminado su trabajo
        List<ComponenteWorker> workersPendientes = componenteWorkerRepository.findAllByMaquinaWorker(maquinaWorker)
                .stream().filter(worker -> !worker.isTrabajoCompletado()).toList();

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
            // Aquí debes buscar la instancia de Maquina en el repositorio y asociarla con el worker.
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
}
