package com.myproyect.springboot.services;

import com.myproyect.springboot.domain.concurrency.*;
import com.myproyect.springboot.domain.distribution.Distribucion;
import com.myproyect.springboot.domain.factory.FabricaGauss;
import com.myproyect.springboot.domain.factory.maquinas.*;
import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import com.myproyect.springboot.model.DistribucionDTO;
import com.myproyect.springboot.model.FabricaGaussDTO;
import com.myproyect.springboot.model.GaltonBoardDTO;
import com.myproyect.springboot.model.maquinas.*;
import com.myproyect.springboot.repos.FabricaGaussRepository;
import com.myproyect.springboot.repos.maquinasRepos.MaquinaRepository;
import com.myproyect.springboot.services.maquinas.MaquinaService;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class FabricaGaussService {

    private static final int NUMBER_OF_MACHINES = 7;

    private final Semaphore semaphore = new Semaphore(1);

    @Autowired
    private GaltonBoardService galtonBoardService;

    @Autowired
    private MaquinaWorkerService maquinaWorkerService;

    @Autowired
    private MaquinaService maquinaService;

    @Autowired
    private MaquinaRepository maquinaRepository;

    private final FabricaGaussRepository fabricaGaussRepository;

    @Autowired
    public FabricaGaussService(final FabricaGaussRepository fabricaGaussRepository, MaquinaService maquinaService) {
        this.fabricaGaussRepository = fabricaGaussRepository;
        this.maquinaService = maquinaService;
    }

    // Metodo para solicitar la detención de la simulación
    public void detenerSimulacion() {
        System.out.println("Solicitando detener la simulación...");
        semaphore.drainPermits(); // Elimina todos los permisos, haciendo que los hilos se detengan
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void iniciarProduccion() {
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_MACHINES);
        List<Future<?>> futures = new ArrayList<>();

        Map<Integer, Integer> indexToGaltonBoardId = new ConcurrentHashMap<>();

        // Crear todos los GaltonBoards antes de iniciar la producción de máquinas
        for (int i = 0; i < NUMBER_OF_MACHINES; i++) {
            GaltonBoard galtonBoard = galtonBoardService.crearYGuardarGaltonBoard(i);

            if (galtonBoard == null || galtonBoard.getId() == null) {
                throw new IllegalStateException("El GaltonBoard no se guardó correctamente y el ID es nulo.");
            }

            indexToGaltonBoardId.put(i, galtonBoard.getId());
            System.out.println("GaltonBoard creado con ID: " + galtonBoard.getId());
        }

        System.out.println("Contenido de indexToGaltonBoardId: " + indexToGaltonBoardId);

        // Iniciar la producción de las máquinas
        for (int i = 0; i < NUMBER_OF_MACHINES; i++) {
            final int index = i;
            final Integer galtonBoardId = indexToGaltonBoardId.get(index);

            Future<?> future = executorService.submit(() -> {
                try {
                    // Recuperar el GaltonBoard
                    System.out.println("Máquina " + index + " intentando recuperar GaltonBoard con ID: " + galtonBoardId);
                    GaltonBoard galtonBoard = galtonBoardService.getEntityById(galtonBoardId);
                    System.out.println("GaltonBoard recuperado con ID: " + galtonBoard.getId());

                    // Crear la máquina usando el DTO
                    MaquinaDTO maquinaDTO = createMaquinaDTO(index, galtonBoardId);
                    Integer maquinaId = maquinaService.create(maquinaDTO);

                    if (maquinaId == null) {
                        throw new IllegalStateException("El ID de la máquina no debe ser nulo después de crear.");
                    }

                    // Recuperar la máquina desde la base de datos
                    Maquina maquina = getEntityById(maquinaId);

                    // Iniciar el trabajo de la máquina
                    maquinaWorkerService.iniciarTrabajo(maquina, galtonBoard);

                    // Simular la caída de bolas y actualizar la distribución
                    galtonBoardService.simularCaidaDeBolas(galtonBoard.getId());

                    // Actualizar la distribución en el GaltonBoard
                    DistribucionDTO distribucionDTO = obtenerDistribucionDTO(galtonBoard);
                    galtonBoardService.actualizarDistribucion(galtonBoard, distribucionDTO);
                    System.out.println("Distribución actualizada para el GaltonBoard con ID: " + galtonBoard.getId());

                    // Ensamblar la máquina verificando si los componentes han terminado
                    MaquinaWorker maquinaWorker = maquinaWorkerService.obtenerMaquinaWorker(maquina.getId());
                    maquinaWorkerService.ensamblarMaquina(maquinaWorker);

                    // Actualizar el estado de la máquina a "FINALIZADA"
                    maquina.setEstado("FINALIZADA");

                    // Crear el DTO específico según el tipo de máquina
                    MaquinaDTO maquinaDTOActualizado = crearMaquinaDTOActualizado(maquina);

                    // Llamar al metodo update con el ID de la máquina y el DTO específico
                    maquinaService.update(maquina.getId(), maquinaDTOActualizado);

                    System.out.println("Máquina " + index + " marcada como FINALIZADA.");
                } catch (Exception e) {
                    System.err.println("Error en la producción de la máquina " + index + ": " + e.getMessage() +
                            " - Stack Trace: " + Arrays.toString(e.getStackTrace()));
                }
            });

            futures.add(future);
        }

        // Apagar el executor una vez se han enviado todas las tareas
        executorService.shutdown();
        try {
            // Esperar hasta que todas las tareas terminen
            boolean terminated = executorService.awaitTermination(20, TimeUnit.MINUTES);

            if (!terminated) {
                System.err.println("No todas las tareas se completaron en el tiempo esperado. Forzando cierre.");
                executorService.shutdownNow();
            } else {
                System.out.println("Todas las tareas de producción completadas.");

                // Mostrar las distribuciones de todos los GaltonBoards de manera compacta
                mostrarDistribucionesDeGaltonBoards(indexToGaltonBoardId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("La espera del cierre del ExecutorService fue interrumpida.");
        }
    }

    private void mostrarDistribucionesDeGaltonBoards(Map<Integer, Integer> indexToGaltonBoardId) {
        System.out.println("Mostrando todas las distribuciones de los GaltonBoards:");
        for (Integer galtonBoardId : indexToGaltonBoardId.values()) {
            GaltonBoard galtonBoard = galtonBoardService.getEntityById(galtonBoardId);
            galtonBoardService.mostrarGaltonBoardCompacto(galtonBoard);
        }
    }

    private MaquinaDTO crearMaquinaDTOActualizado(Maquina maquina) {
        // Aquí crear y retornar el DTO específico basado en el tipo de la máquina.
        // Similar a la lógica utilizada en la creación para cada tipo de máquina.
        MaquinaDTO maquinaDTO;
        switch (maquina.getTipo().toUpperCase()) {
            case "BINOMIAL":
                maquinaDTO = new MaquinaDistribucionBinomialDTO();
                ((MaquinaDistribucionBinomialDTO) maquinaDTO).setNumEnsayos(((MaquinaDistribucionBinomial) maquina).getNumEnsayos());
                ((MaquinaDistribucionBinomialDTO) maquinaDTO).setProbabilidadExito(((MaquinaDistribucionBinomial) maquina).getProbabilidadExito());
                break;
            case "GEOMETRICA":
                maquinaDTO = new MaquinaDistribucionGeometricaDTO();
                ((MaquinaDistribucionGeometricaDTO) maquinaDTO).setProbabilidadExito(((MaquinaDistribucionGeometrica) maquina).getProbabilidadExito());
                ((MaquinaDistribucionGeometricaDTO) maquinaDTO).setMaximoEnsayos(((MaquinaDistribucionGeometrica) maquina).getMaximoEnsayos());
                break;
            case "EXPONENCIAL":
                maquinaDTO = new MaquinaDistribucionExponencialDTO();
                ((MaquinaDistribucionExponencialDTO) maquinaDTO).setLambda(((MaquinaDistribucionExponencial) maquina).getLambda());
                ((MaquinaDistribucionExponencialDTO) maquinaDTO).setMaximoValor(((MaquinaDistribucionExponencial) maquina).getMaximoValor());
                break;
            case "NORMAL":
                maquinaDTO = new MaquinaDistribucionNormalDTO();
                ((MaquinaDistribucionNormalDTO) maquinaDTO).setMedia(((MaquinaDistribucionNormal) maquina).getMedia());
                ((MaquinaDistribucionNormalDTO) maquinaDTO).setDesviacionEstandar(((MaquinaDistribucionNormal) maquina).getDesviacionEstandar());
                ((MaquinaDistribucionNormalDTO) maquinaDTO).setMaximoValor(((MaquinaDistribucionNormal) maquina).getMaximoValor());
                break;
            case "UNIFORME":
                maquinaDTO = new MaquinaDistribucionUniformeDTO();
                ((MaquinaDistribucionUniformeDTO) maquinaDTO).setNumValores(((MaquinaDistribucionUniforme) maquina).getNumValores());
                break;
            case "CUSTOM":
                maquinaDTO = new MaquinaDistribucionCustomDTO();
                ((MaquinaDistribucionCustomDTO) maquinaDTO).setProbabilidadesPersonalizadas(((MaquinaDistribucionCustom) maquina).getProbabilidadesPersonalizadas());
                break;
            case "POISSON":
                maquinaDTO = new MaquinaDistribucionPoissonDTO();
                ((MaquinaDistribucionPoissonDTO) maquinaDTO).setLambda(((MaquinaDistribucionPoisson) maquina).getLambda());
                ((MaquinaDistribucionPoissonDTO) maquinaDTO).setMaximoValor(((MaquinaDistribucionPoisson) maquina).getMaximoValor());
                break;
            default:
                throw new IllegalArgumentException("Tipo de máquina desconocido: " + maquina.getTipo());
        }

        // Asignar los valores comunes a todos los tipos de máquina
        maquinaDTO.setId(maquina.getId());
        maquinaDTO.setEstado(maquina.getEstado());
        maquinaDTO.setNumeroComponentesRequeridos(maquina.getNumeroComponentesRequeridos());
        maquinaDTO.setTipo(maquina.getTipo());
        maquinaDTO.setGaltonBoardId(maquina.getGaltonBoard().getId());

        return maquinaDTO;
    }


    // Metodo auxiliar para obtener DistribucionDTO
    private DistribucionDTO obtenerDistribucionDTO(GaltonBoard galtonBoard) {
        DistribucionDTO distribucionDTO = new DistribucionDTO();
        Distribucion distribucionActual = galtonBoard.getDistribucion();

        if (distribucionActual != null) {
            distribucionDTO.setDatos(new HashMap<>(distribucionActual.getDatos()));
            distribucionDTO.setNumBolas(distribucionActual.getNumBolas());
            distribucionDTO.setNumContenedores(distribucionActual.getNumContenedores());
        } else {
            distribucionDTO.setDatos(new HashMap<>());
            distribucionDTO.setNumBolas(galtonBoard.getNumBolas());
            distribucionDTO.setNumContenedores(galtonBoard.getNumContenedores());

            for (int x = 1; x <= galtonBoard.getNumContenedores(); x++) {
                distribucionDTO.getDatos().put("Contenedor " + x, 0);
            }
        }
        return distribucionDTO;
    }


    // Metodo para crear el DTO de una máquina basado en el índice y el ID del GaltonBoard
    public MaquinaDTO createMaquinaDTO(int index, Integer galtonBoardId) {
        MaquinaDTO maquinaDTO;

        switch (index) {
            case 0:
                MaquinaDistribucionBinomialDTO binomialDTO = new MaquinaDistribucionBinomialDTO();
                binomialDTO.setNumEnsayos(10);
                binomialDTO.setProbabilidadExito(0.9);
                binomialDTO.setNumeroComponentesRequeridos(5);
                binomialDTO.setTipo("BINOMIAL");
                binomialDTO.setEstado("INICIALIZADO");
                binomialDTO.setGaltonBoardId(galtonBoardId);
                maquinaDTO = binomialDTO;
                break;

            case 1:
                MaquinaDistribucionGeometricaDTO geometricaDTO = new MaquinaDistribucionGeometricaDTO();
                geometricaDTO.setMaximoEnsayos(20);
                geometricaDTO.setProbabilidadExito(0.9);
                geometricaDTO.setNumeroComponentesRequeridos(5);
                geometricaDTO.setTipo("GEOMETRICA");
                geometricaDTO.setEstado("INICIALIZADO");
                geometricaDTO.setGaltonBoardId(galtonBoardId);
                maquinaDTO = geometricaDTO;
                break;

            case 2:
                MaquinaDistribucionExponencialDTO exponencialDTO = new MaquinaDistribucionExponencialDTO();
                exponencialDTO.setMaximoValor(10);
                exponencialDTO.setLambda(2.3);
                exponencialDTO.setNumeroComponentesRequeridos(2);
                exponencialDTO.setTipo("EXPONENCIAL");
                exponencialDTO.setEstado("INICIALIZADO");
                exponencialDTO.setGaltonBoardId(galtonBoardId);
                maquinaDTO = exponencialDTO;
                break;

            case 3:
                MaquinaDistribucionNormalDTO normalDTO = new MaquinaDistribucionNormalDTO();
                normalDTO.setMedia(5);
                normalDTO.setDesviacionEstandar(1.5);
                normalDTO.setMaximoValor(10);
                normalDTO.setNumeroComponentesRequeridos(2);
                normalDTO.setTipo("NORMAL");
                normalDTO.setEstado("INICIALIZADO");
                normalDTO.setGaltonBoardId(galtonBoardId);
                maquinaDTO = normalDTO;
                break;

            case 4:
                MaquinaDistribucionPoissonDTO poissonDTO = new MaquinaDistribucionPoissonDTO();
                poissonDTO.setLambda(2);
                poissonDTO.setMaximoValor(10);
                poissonDTO.setNumeroComponentesRequeridos(2);
                poissonDTO.setTipo("POISSON");
                poissonDTO.setEstado("INICIALIZADO");
                poissonDTO.setGaltonBoardId(galtonBoardId);
                maquinaDTO = poissonDTO;
                break;

            case 5:
                MaquinaDistribucionUniformeDTO uniformeDTO = new MaquinaDistribucionUniformeDTO();
                uniformeDTO.setNumValores(10);
                uniformeDTO.setNumeroComponentesRequeridos(2);
                uniformeDTO.setTipo("UNIFORME");
                uniformeDTO.setEstado("INICIALIZADO");
                uniformeDTO.setGaltonBoardId(galtonBoardId);
                maquinaDTO = uniformeDTO;
                break;

            case 6:
                MaquinaDistribucionCustomDTO customDTO = new MaquinaDistribucionCustomDTO();
                customDTO.setNumeroComponentesRequeridos(2);
                customDTO.setTipo("CUSTOM");
                customDTO.setEstado("INICIALIZADO");
                customDTO.setGaltonBoardId(galtonBoardId);
                maquinaDTO = customDTO;
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + index);
        }
        return maquinaDTO;
    }

    // Métodos CRUD para la gestión de la fábrica.
    public List<FabricaGaussDTO> findAll() {
        return fabricaGaussRepository.findAll(Sort.by("id")).stream()
                .map(fabrica -> mapToDTO(fabrica, new FabricaGaussDTO()))
                .collect(Collectors.toList());
    }

    public void delete(final Integer id) {
        fabricaGaussRepository.deleteById(id);
    }

    public Integer create(final FabricaGaussDTO fabricaGaussDTO) {
        FabricaGauss fabricaGauss = new FabricaGauss();
        mapToEntity(fabricaGaussDTO, fabricaGauss);

        return fabricaGaussRepository.save(fabricaGauss).getId();
    }

    public void update(final Integer id, final FabricaGaussDTO fabricaGaussDTO) {
        FabricaGauss fabricaGauss = fabricaGaussRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        mapToEntity(fabricaGaussDTO, fabricaGauss);

        // Persistir los cambios
        fabricaGaussRepository.save(fabricaGauss);
    }

    public FabricaGaussDTO get(final Integer id) {
        FabricaGauss fabricaGauss = fabricaGaussRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        return mapToDTO(fabricaGauss, new FabricaGaussDTO());
    }

    public FabricaGauss mapToEntity(final FabricaGaussDTO fabricaGaussDTO, final FabricaGauss fabricaGauss) {
        fabricaGauss.setNombre(fabricaGaussDTO.getNombre());

        return fabricaGauss;
    }

    public FabricaGaussDTO mapToDTO(final FabricaGauss fabricaGauss, final FabricaGaussDTO fabricaGaussDTO) {
        fabricaGaussDTO.setId(fabricaGauss.getId());
        fabricaGaussDTO.setNombre(fabricaGauss.getNombre());
        fabricaGaussDTO.setDateCreated(fabricaGauss.getDateCreated());

        return fabricaGaussDTO;
    }

    public Maquina getEntityById(Integer id) {
        return maquinaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Máquina no encontrada con ID: " + id));
    }
}
