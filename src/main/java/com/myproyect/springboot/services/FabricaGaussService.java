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

    // Metodo para iniciar la simulación de la fábrica
    public void iniciarProduccion() {
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_MACHINES);
        List<Future<?>> futures = new ArrayList<>();

        Map<Integer, Integer> indexToGaltonBoardId = new ConcurrentHashMap<>();

        // Crear todos los GaltonBoards antes de iniciar la producción de máquinas
        for (int i = 0; i < NUMBER_OF_MACHINES; i++) {
            GaltonBoard galtonBoard = crearYGuardarGaltonBoard(i);

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
                    if (semaphore.availablePermits() == 0) {
                        System.out.println("Deteniendo la simulación de la máquina " + index + " debido a la señal de detención.");
                        return;
                    }

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
                    galtonBoardService.mostrarDistribucion(galtonBoard.getId());

                    // Actualizar la distribución en el GaltonBoard
                    DistribucionDTO distribucionDTO = obtenerDistribucionDTO(galtonBoard);
                    galtonBoardService.actualizarDistribucion(galtonBoard, distribucionDTO);
                    System.out.println("Distribución actualizada para el GaltonBoard con ID: " + galtonBoard.getId());

                    // Ensamblar la máquina verificando si los componentes han terminado
                    MaquinaWorker maquinaWorker = maquinaWorkerService.obtenerMaquinaWorker(maquina.getId());
                    maquinaWorkerService.ensamblarMaquina(maquinaWorker);

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
            executorService.awaitTermination(20, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Método auxiliar para obtener DistribucionDTO
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


    // Metodo para crear y guardar un GaltonBoard
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GaltonBoard crearYGuardarGaltonBoard(int index) {
        GaltonBoard galtonBoard = new GaltonBoard();
        galtonBoard.setNumBolas(500 + (index * 100));
        galtonBoard.setNumContenedores(3);
        galtonBoard.setEstado("INICIALIZADO");

        GaltonBoardDTO galtonBoardDTO = galtonBoardService.mapToDTO(galtonBoard, new GaltonBoardDTO());
        GaltonBoard savedGaltonBoard = galtonBoardService.create(galtonBoardDTO);

        // Forzar el flush para asegurar que se escriba en la base de datos
        galtonBoardService.flush();

        System.out.println("GaltonBoard creado y guardado con ID: " + savedGaltonBoard.getId());

        return savedGaltonBoard;
    }

    // Metodo para crear el DTO de una máquina basado en el índice y el ID del GaltonBoard
    public MaquinaDTO createMaquinaDTO(int index, Integer galtonBoardId) {
        MaquinaDTO maquinaDTO;

        switch (index) {
            case 0:
                MaquinaDistribucionBinomialDTO binomialDTO = new MaquinaDistribucionBinomialDTO();
                binomialDTO.setNumEnsayos(10);
                binomialDTO.setProbabilidadExito(0.5);
                binomialDTO.setNumeroComponentesRequeridos(5);
                binomialDTO.setTipo("BINOMIAL");
                binomialDTO.setEstado("INICIALIZADO");
                binomialDTO.setGaltonBoardId(galtonBoardId);
                maquinaDTO = binomialDTO;
                break;

            case 1:
                MaquinaDistribucionGeometricaDTO geometricaDTO = new MaquinaDistribucionGeometricaDTO();
                geometricaDTO.setMaximoEnsayos(20);
                geometricaDTO.setProbabilidadExito(0.7);
                geometricaDTO.setNumeroComponentesRequeridos(5);
                geometricaDTO.setTipo("GEOMETRICA");
                geometricaDTO.setEstado("INICIALIZADO");
                geometricaDTO.setGaltonBoardId(galtonBoardId);
                maquinaDTO = geometricaDTO;
                break;

            case 2:
                MaquinaDistribucionExponencialDTO exponencialDTO = new MaquinaDistribucionExponencialDTO();
                exponencialDTO.setMaximoValor(30);
                exponencialDTO.setLambda(0.8);
                exponencialDTO.setNumeroComponentesRequeridos(5);
                exponencialDTO.setTipo("EXPONENCIAL");
                exponencialDTO.setEstado("INICIALIZADO");
                exponencialDTO.setGaltonBoardId(galtonBoardId);
                maquinaDTO = exponencialDTO;
                break;

            case 3:
                MaquinaDistribucionNormalDTO normalDTO = new MaquinaDistribucionNormalDTO();
                normalDTO.setMedia(40);
                normalDTO.setDesviacionEstandar(0.5);
                normalDTO.setMaximoValor(50);
                normalDTO.setNumeroComponentesRequeridos(5);
                normalDTO.setTipo("NORMAL");
                normalDTO.setEstado("INICIALIZADO");
                normalDTO.setGaltonBoardId(galtonBoardId);
                maquinaDTO = normalDTO;
                break;

            case 4:
                MaquinaDistribucionPoissonDTO poissonDTO = new MaquinaDistribucionPoissonDTO();
                poissonDTO.setLambda(50);
                poissonDTO.setMaximoValor(60);
                poissonDTO.setNumeroComponentesRequeridos(5);
                poissonDTO.setTipo("POISSON");
                poissonDTO.setEstado("INICIALIZADO");
                poissonDTO.setGaltonBoardId(galtonBoardId);
                maquinaDTO = poissonDTO;
                break;

            case 5:
                MaquinaDistribucionUniformeDTO uniformeDTO = new MaquinaDistribucionUniformeDTO();
                uniformeDTO.setNumValores(10);
                uniformeDTO.setNumeroComponentesRequeridos(5);
                uniformeDTO.setTipo("UNIFORME");
                uniformeDTO.setEstado("INICIALIZADO");
                uniformeDTO.setGaltonBoardId(galtonBoardId);
                maquinaDTO = uniformeDTO;
                break;

            case 6:
                MaquinaDistribucionCustomDTO customDTO = new MaquinaDistribucionCustomDTO();
                customDTO.setNumeroComponentesRequeridos(5);
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
