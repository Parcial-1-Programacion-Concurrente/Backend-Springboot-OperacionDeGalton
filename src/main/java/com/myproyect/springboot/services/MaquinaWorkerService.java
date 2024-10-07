package com.myproyect.springboot.services;

import com.myproyect.springboot.domain.concurrency.ComponenteWorker;
import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import com.myproyect.springboot.model.MaquinaWorkerDTO;
import com.myproyect.springboot.domain.concurrency.MaquinaWorker;
import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.repos.ComponenteWorkerRepository;
import com.myproyect.springboot.repos.MaquinaWorkerRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class MaquinaWorkerService {

    private final MaquinaWorkerRepository maquinaWorkerRepository;
    private final ComponenteWorkerRepository componenteWorkerRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Autowired
    public MaquinaWorkerService(final MaquinaWorkerRepository maquinaWorkerRepository,
                                final ComponenteWorkerRepository componenteWorkerRepository) {
        this.maquinaWorkerRepository = maquinaWorkerRepository;
        this.componenteWorkerRepository = componenteWorkerRepository;
    }

    // Iniciar el ensamblaje de la máquina usando los ComponenteWorkers
    public void iniciarEnsamblaje(MaquinaWorker maquinaWorker) {
        System.out.println("Iniciando ensamblaje para la máquina de tipo: " + maquinaWorker.getMaquina().getTipo());
        maquinaWorker.setExecutor(executorService);

        // Crear y agregar ComponenteWorkers al MaquinaWorker
        maquinaWorker.getComponenteWorkers().forEach(this::agregarComponenteWorker);

        // Ejecutar el MaquinaWorker en un nuevo hilo
        executorService.submit(maquinaWorker);
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
        List<ComponenteWorker> workersPendientes = componenteWorkerRepository.findAllByMaquinaId(maquinaWorker.getMaquina().getId())
                .stream().filter(worker -> !worker.isTrabajoCompletado()).toList();

        if (workersPendientes.isEmpty()) {
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

    public MaquinaWorkerDTO get(final Long id) {
        return maquinaWorkerRepository.findById(id)
                .map(worker -> mapToDTO(worker, new MaquinaWorkerDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final MaquinaWorkerDTO maquinaWorkerDTO) {
        MaquinaWorker maquinaWorker = new MaquinaWorker();
        mapToEntity(maquinaWorkerDTO, maquinaWorker);
        return maquinaWorkerRepository.save(maquinaWorker).getId();
    }

    public void update(final Long id, final MaquinaWorkerDTO maquinaWorkerDTO) {
        MaquinaWorker maquinaWorker = maquinaWorkerRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(maquinaWorkerDTO, maquinaWorker);
        maquinaWorkerRepository.save(maquinaWorker);
    }

    public void delete(final Long id) {
        maquinaWorkerRepository.deleteById(id);
    }

    private MaquinaWorkerDTO mapToDTO(final MaquinaWorker worker, final MaquinaWorkerDTO dto) {
        dto.setId(worker.getId());
        dto.setMaquinaId(worker.getMaquina().getId());
        return dto;
    }

    private MaquinaWorker mapToEntity(final MaquinaWorkerDTO dto, final MaquinaWorker worker) {
        worker.setMaquinaId(dto.getMaquinaId());
        return worker;
    }
}
