package com.myproyect.springboot.services;

import com.myproyect.springboot.domain.concurrency.*;
import com.myproyect.springboot.domain.distribution.Distribucion;
import com.myproyect.springboot.domain.factory.maquinas.*;
import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import com.myproyect.springboot.model.DistribucionDTO;
import com.myproyect.springboot.model.FabricaGaussDTO;
import com.myproyect.springboot.model.GaltonBoardDTO;
import com.myproyect.springboot.model.maquinas.*;
import com.myproyect.springboot.repos.FabricaGaussRepository;
import com.myproyect.springboot.services.maquinas.MaquinaService;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Service
@Transactional
public class FabricaGaussService {

    private static final int NUMBER_OF_MACHINES = 7;

    private final Semaphore semaphore = new Semaphore(1);

    @Autowired
    private GaltonBoardService galtonBoardService;

    @Autowired
    private MaquinaWorkerService maquinaWorkerService;

    @Autowired
    private MaquinaService maquinaService;

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

    public void iniciarProduccion() {
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_MACHINES);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_MACHINES; i++) {
            final int index = i;  // Hacer final para usar en la expresión lambda

            Future<?> future = executorService.submit(() -> {
                try {
                    // Verificar si hay permisos disponibles para continuar la ejecución
                    if (semaphore.availablePermits() == 0) {
                        System.out.println("Deteniendo la simulación de la máquina " + index + " debido a la señal de detención.");
                        return;
                    }

                    // Crear la máquina usando el DTO
                    MaquinaDTO maquinaDTO = createMaquinaDTO(index);
                    Integer maquinaId = maquinaService.create(maquinaDTO);

                    if (maquinaId == null) {
                        throw new IllegalStateException("El ID de la máquina no debe ser nulo después de crear.");
                    }

                    Maquina maquina = maquinaService.ToEntity(maquinaDTO);

                    // Obtener el GaltonBoard y su DTO
                    GaltonBoard galtonBoard = galtonBoardService.getEntityById(index);

                    if (galtonBoard == null || galtonBoard.getId() == null) {
                        throw new IllegalStateException("El GaltonBoard no debe ser nulo después de obtenerlo.");
                    }
                    Integer galtonBoardId = galtonBoard.getId();

                    // Iniciar el trabajo de la máquina
                    maquinaWorkerService.iniciarTrabajo(maquina, galtonBoard);

                    // Simular la caída de bolas y actualizar la distribución
                    galtonBoardService.simularCaidaDeBolas(galtonBoardId);

                    galtonBoardService.mostrarDistribucion(galtonBoard.getId());

                    // Verificar si hay permisos disponibles para continuar la ejecución
                    if (semaphore.availablePermits() == 0) {
                        System.out.println("Deteniendo la simulación de la máquina " + index + " después de la simulación de bolas.");
                        return;
                    }

                    // Crear una instancia de DistribucionDTO
                    DistribucionDTO distribucionDTO = new DistribucionDTO();

                    // Obtener la distribución actual del GaltonBoard (si existe)
                    Distribucion distribucionActual = galtonBoard.getDistribucion();

                    // Verificar si existe una distribución para copiar los datos
                    if (distribucionActual != null) {
                        // Copiar los datos de la distribución existente al DTO
                        distribucionDTO.setDatos(new HashMap<>(distribucionActual.getDatos()));
                        distribucionDTO.setNumBolas(distribucionActual.getNumBolas());
                        distribucionDTO.setNumContenedores(distribucionActual.getNumContenedores());
                    } else {
                        // Si no existe una distribución, inicializar con valores por defecto o nuevos
                        distribucionDTO.setDatos(new HashMap<>());
                        distribucionDTO.setNumBolas(galtonBoard.getNumBolas());
                        distribucionDTO.setNumContenedores(galtonBoard.getNumContenedores());

                        // Simular una nueva distribución inicial (esto es un ejemplo básico)
                        for (int x = 1; x <= galtonBoard.getNumContenedores(); x++) {
                            distribucionDTO.getDatos().put("Contenedor " + x, 0);
                        }
                    }

// Actualizar la distribución en el GaltonBoard usando el DTO
                    galtonBoardService.actualizarDistribucion(galtonBoard, distribucionDTO);

                    System.out.println("Distribución actualizada para el GaltonBoard con ID: " + galtonBoard.getId());


                    // Ensamblar la máquina verificando si los componentes han terminado
                    MaquinaWorker maquinaWorker = maquinaWorkerService.obtenerMaquinaWorker(maquina.getId());
                    maquinaWorkerService.ensamblarMaquina(maquinaWorker);

                } catch (Exception e) {
                    System.err.println("Error en la producción de la máquina " + index + ": " + e.getMessage() + " - Stack Trace: " + Arrays.toString(e.getStackTrace()));

                }
            });

            futures.add(future);
        }

        // Esperar a que todas las tareas finalicen
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.err.println("Error al esperar la finalización de la tarea: " + e.getMessage());
            }
        }

        executorService.shutdown();
    }

    public MaquinaDTO createMaquinaDTO(int index) {
        MaquinaDTO maquinaDTO;

        GaltonBoard galtonBoard = crearYGuardarGaltonBoard(index);
        Integer galtonId = galtonBoard.getId();

        switch (index) {
            case 0:
                MaquinaDistribucionBinomialDTO binomialDTO = new MaquinaDistribucionBinomialDTO();
                binomialDTO.setNumEnsayos(10);
                binomialDTO.setProbabilidadExito(0.5);
                binomialDTO.setNumeroComponentesRequeridos(3);
                binomialDTO.setTipo("BINOMIAL");
                binomialDTO.setEstado("INICIALIZADO");
                binomialDTO.setGaltonBoardId(galtonId);
                maquinaDTO = binomialDTO;
                break;

            case 1:
                MaquinaDistribucionGeometricaDTO geometricaDTO = new MaquinaDistribucionGeometricaDTO();
                geometricaDTO.setMaximoEnsayos(20);
                geometricaDTO.setProbabilidadExito(0.7);
                geometricaDTO.setNumeroComponentesRequeridos(3);
                geometricaDTO.setTipo("GEOMETRICA");
                geometricaDTO.setEstado("INICIALIZADO");
                geometricaDTO.setGaltonBoardId(galtonId);
                maquinaDTO = geometricaDTO;
                break;

            case 2:
                MaquinaDistribucionExponencialDTO exponencialDTO = new MaquinaDistribucionExponencialDTO();
                exponencialDTO.setMaximoValor(30);
                exponencialDTO.setLambda(0.8);
                exponencialDTO.setNumeroComponentesRequeridos(3);
                exponencialDTO.setTipo("EXPONENCIAL");
                exponencialDTO.setEstado("INICIALIZADO");
                exponencialDTO.setGaltonBoardId(galtonId);
                maquinaDTO = exponencialDTO;
                break;

            case 3:
                MaquinaDistribucionNormalDTO normalDTO = new MaquinaDistribucionNormalDTO();
                normalDTO.setMedia(40);
                normalDTO.setDesviacionEstandar(0.5);
                normalDTO.setMaximoValor(50);
                normalDTO.setNumeroComponentesRequeridos(3);
                normalDTO.setTipo("NORMAL");
                normalDTO.setEstado("INICIALIZADO");
                normalDTO.setGaltonBoardId(galtonId);
                maquinaDTO = normalDTO;
                break;

            case 4:
                MaquinaDistribucionPoissonDTO poissonDTO = new MaquinaDistribucionPoissonDTO();
                poissonDTO.setLambda(50);
                poissonDTO.setMaximoValor(60);
                poissonDTO.setNumeroComponentesRequeridos(3);
                poissonDTO.setTipo("POISSON");
                poissonDTO.setEstado("INICIALIZADO");
                poissonDTO.setGaltonBoardId(galtonId);
                maquinaDTO = poissonDTO;
                break;

            case 5:
                MaquinaDistribucionUniformeDTO uniformeDTO = new MaquinaDistribucionUniformeDTO();
                uniformeDTO.setNumValores(10);
                uniformeDTO.setNumeroComponentesRequeridos(3);
                uniformeDTO.setTipo("UNIFORME");
                uniformeDTO.setEstado("INICIALIZADO");
                uniformeDTO.setGaltonBoardId(galtonId);
                maquinaDTO = uniformeDTO;
                break;

            case 6:
                MaquinaDistribucionCustomDTO customDTO = new MaquinaDistribucionCustomDTO();
                customDTO.setNumeroComponentesRequeridos(3);
                customDTO.setTipo("CUSTOM");
                customDTO.setEstado("INICIALIZADO");
                customDTO.setGaltonBoardId(galtonId);
                maquinaDTO = customDTO;
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + index);
        }
        return maquinaDTO;
    }



    private GaltonBoard crearYGuardarGaltonBoard(int index) {
        GaltonBoard galtonBoard = new GaltonBoard();
        galtonBoard.setNumBolas(500 + (index * 100));
        galtonBoard.setNumContenedores(3);
        galtonBoard.setEstado("INICIALIZADO");

        GaltonBoardDTO galtonBoardDTO = galtonBoardService.mapToDTO(galtonBoard, new GaltonBoardDTO());
        GaltonBoard galtonBoards = galtonBoardService.create(galtonBoardDTO);

        // Verificar que se ha guardado correctamente
        if (galtonBoards.getId() == null) {
            throw new IllegalStateException("El ID del GaltonBoard no debe ser nulo después de crear.");
        }

        return galtonBoards;
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

    public FabricaGauss getEntityById(final Integer id) {
        return fabricaGaussRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fábrica no encontrada con ID: " + id));
    }

}

