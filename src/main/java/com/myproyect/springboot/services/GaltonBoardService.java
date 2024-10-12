package com.myproyect.springboot.services;

import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import com.myproyect.springboot.domain.distribution.Distribucion;
import com.myproyect.springboot.model.DistribucionDTO;
import com.myproyect.springboot.model.GaltonBoardDTO;
import com.myproyect.springboot.repos.DistribucionRepository;
import com.myproyect.springboot.repos.GaltonBoardRepository;
import com.myproyect.springboot.services.maquinas.MaquinaService;
import com.myproyect.springboot.util.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GaltonBoardService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DistribucionRepository distribucionRepository;

    @Autowired
    private MaquinaService maquinaService;

    private final GaltonBoardRepository galtonBoardRepository;

    @Autowired
    public GaltonBoardService(final GaltonBoardRepository galtonBoardRepository) {
        this.galtonBoardRepository = galtonBoardRepository;
    }

    public void simularCaidaDeBolas(Integer galtonBoardId) {
        GaltonBoard galtonBoard = galtonBoardRepository.findById(galtonBoardId)
                .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + galtonBoardId));

        // Inicializar el estado de la simulación.
        galtonBoard.setEstado("EN_SIMULACION");

        // Variables para la simulación.
        int numBolas = galtonBoard.getNumBolas();
        int[] contenedores = new int[galtonBoard.getNumContenedores()];
        Random random = new Random();
        int bolasPorLote = 100;
        int numBolasProcesadas = 0;

        // Guardar el estado inicial.
        galtonBoardRepository.save(galtonBoard);

        // Obtener la instancia de `Maquina` asociada al `GaltonBoard`.
        Maquina maquina = maquinaService.getByGaltonBoardId(galtonBoardId); // Asume que este método existe en `maquinaService`.

        // Simular la caída de bolas en lotes de 100.
        for (int i = 0; i < numBolas; i++) {
            // Verificar si hemos alcanzado el límite de bolas.
            if (numBolasProcesadas >= numBolas) {
                break;
            }

            // Determinar a qué contenedor cae cada bola.
            int contenedorSeleccionado = 0;
            for (int j = 0; j < galtonBoard.getNumContenedores() - 1; j++) {
                if (random.nextBoolean()) {
                    contenedorSeleccionado++;
                }
            }
            contenedores[contenedorSeleccionado]++;
            numBolasProcesadas++;

            // Actualizar la distribución cada 100 bolas o al final de la simulación.
            if (numBolasProcesadas % bolasPorLote == 0 || numBolasProcesadas == numBolas) {
                // Convertir y guardar la distribución.
                Map<String, Integer> datosDistribucion = convertirAFormatoDistribucion(contenedores);
                actualizarDistribucion(galtonBoard, datosDistribucion);

                // Mostrar la distribución actualizada.
                mostrarDistribucionEnProceso(galtonBoard, datosDistribucion, maquina);

                System.out.println("Actualizando distribución con " + numBolasProcesadas + " bolas procesadas.");

                // Simular la espera de 2 segundos antes de continuar con la siguiente caída de bolas.
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Simulación interrumpida para GaltonBoard con ID: " + galtonBoardId);
                    break;
                }
            }

            // Verificar si es el último lote.
            if (i == numBolas - 1) {
                // Convertir y guardar la distribución.
                Map<String, Integer> datosDistribucionFinal = convertirAFormatoDistribucion(contenedores);
                actualizarDistribucion(galtonBoard, datosDistribucionFinal);

                // Mostrar la distribución final.
                mostrarDistribucionEnProceso(galtonBoard, datosDistribucionFinal, maquina);
                System.out.println("Distribución finalizada con " + numBolasProcesadas + " bolas procesadas.");
            }
        }

        // Marcar la simulación como finalizada.
        galtonBoard.setEstado("FINALIZADA");
        galtonBoardRepository.save(galtonBoard);

        System.out.println("Simulación de caída de bolas finalizada para el GaltonBoard con ID: " + galtonBoardId);
    }


    private void mostrarDistribucionEnProceso(GaltonBoard galtonBoard, Map<String, Integer> datosDistribucion, Maquina maquina) {
        int maxBolas = datosDistribucion.values().stream().max(Integer::compareTo).orElse(1);
        int numBolasProcesadas = datosDistribucion.values().stream().mapToInt(Integer::intValue).sum();
        int numMaxBolas = galtonBoard.getNumBolas();

        if (numBolasProcesadas < numMaxBolas) {
            System.out.println("Distribución en proceso para GaltonBoard con ID: " + galtonBoard.getId());
            for (Map.Entry<String, Integer> entry : datosDistribucion.entrySet()) {
                String contenedor = entry.getKey();
                int bolas = entry.getValue();
                int longitudBarra = (int) ((bolas / (double) maxBolas) * 50);
                String barra = "█".repeat(longitudBarra);
                System.out.printf("%-10s: %-50s (%d)%n", contenedor, barra, bolas);
            }
            System.out.println();
        } else {
            String tipoMaquina = maquina.getTipo();
            System.out.println("Simulación finalizada para GaltonBoard con ID: " + galtonBoard.getId() +
                    " y distribución de tipo: " + tipoMaquina);
            for (Map.Entry<String, Integer> entry : datosDistribucion.entrySet()) {
                String contenedor = entry.getKey();
                int bolas = entry.getValue();
                int longitudBarra = (int) ((bolas / (double) maxBolas) * 50);
                String barra = "█".repeat(longitudBarra);
                System.out.printf("%-10s: %-50s (%d)%n", contenedor, barra, bolas);
            }
            System.out.println();
        }
    }


    // Metodo auxiliar para actualizar la distribución de un GaltonBoard con una DistribucionDTO.
    @Transactional
    public void actualizarDistribucion(GaltonBoard galtonBoard, Map<String, Integer> datos) {
        System.out.println("Actualizando distribución para GaltonBoard con ID: " + galtonBoard.getId());

        // Verificar si el GaltonBoard está persistido.
        if (galtonBoard.getId() == null) {
            galtonBoard = galtonBoardRepository.save(galtonBoard);
            System.out.println("GaltonBoard guardado con ID: " + galtonBoard.getId());
        }

        // Obtener la instancia de la distribución.
        Distribucion distribucion = galtonBoard.getDistribucion();
        if (distribucion == null) {
            // Crear nueva Distribucion y asociarla al GaltonBoard.
            distribucion = new Distribucion();
            distribucion.setGaltonBoard(galtonBoard);
            galtonBoard.setDistribucion(distribucion);
        }

        // Actualizar los datos de la distribución.
        distribucion.setDatos(datos);
        distribucion.setNumBolas(galtonBoard.getNumBolas());
        distribucion.setNumContenedores(galtonBoard.getNumContenedores());

        // Guardar la distribución.
        distribucion = distribucionRepository.save(distribucion);
        galtonBoard.setDistribucion(distribucion);

        System.out.println("Distribución actualizada para GaltonBoard con ID: " + galtonBoard.getId());
    }

    // Metodo auxiliar para convertir la distribución a un formato de Map<String, Integer>.
    private Map<String, Integer> convertirAFormatoDistribucion(int[] contenedores) {
        Map<String, Integer> distribucionMap = new HashMap<>();
        for (int i = 0; i < contenedores.length; i++) {
            distribucionMap.put("Contenedor " + i, contenedores[i]);
        }
        return distribucionMap;
    }

    // Metodo para mostrar la distribución de acuerdo al tipo
    public void mostrarDistribucion(Integer galtonBoardId) {
        GaltonBoard galtonBoard = galtonBoardRepository.findById(galtonBoardId)
                .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + galtonBoardId));

        // Verificar que la simulación haya finalizado
        if ("FINALIZADA".equals(galtonBoard.getEstado())) {
            Distribucion distribucion = galtonBoard.getDistribucion();

            // Verificar que la distribución y los datos no sean nulos
            if (distribucion != null) {
                Map<String, Integer> datos = distribucion.getDatos();

                if (datos != null && !datos.isEmpty()) {
                    System.out.println("Distribución de bolas para el GaltonBoard con ID: " + galtonBoardId);
                    datos.forEach((key, value) -> {
                        System.out.println(key + ": " + "*".repeat(value));
                    });
                } else {
                    System.out.println("No hay datos de distribución disponibles para el GaltonBoard con ID: " + galtonBoardId);
                }
            } else {
                System.out.println("Distribución no encontrada para el GaltonBoard con ID: " + galtonBoardId);
            }
        } else {
            System.out.println("La simulación aún no ha finalizado para el GaltonBoard con ID: " + galtonBoardId);
        }
    }


    // Métodos CRUD para gestionar GaltonBoard.
    public List<GaltonBoardDTO> findAll() {
        return galtonBoardRepository.findAll(Sort.by("id")).stream()
                .map(board -> mapToDTO(board, new GaltonBoardDTO()))
                .collect(Collectors.toList());
    }

    public GaltonBoardDTO get(final Integer id) {
        return galtonBoardRepository.findById(id)
                .map(board -> mapToDTO(board, new GaltonBoardDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public GaltonBoard create(final GaltonBoardDTO galtonBoardDTO) {
        GaltonBoard galtonBoard = new GaltonBoard();
        mapToEntity(galtonBoardDTO, galtonBoard);
        return galtonBoardRepository.save(galtonBoard);
    }


    public void update(final Integer id, final GaltonBoardDTO galtonBoardDTO) {
        GaltonBoard galtonBoard = galtonBoardRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(galtonBoardDTO, galtonBoard);
        galtonBoardRepository.save(galtonBoard);
    }

    public void delete(final Integer id) {
        galtonBoardRepository.deleteById(id);
    }

    public GaltonBoardDTO mapToDTO(final GaltonBoard galtonBoard, final GaltonBoardDTO dto) {
        dto.setId(galtonBoard.getId());
        dto.setNumBolas(galtonBoard.getNumBolas());
        dto.setNumContenedores(galtonBoard.getNumContenedores());
        dto.setEstado(galtonBoard.getEstado());
        return dto;
    }

    public GaltonBoard mapToEntity(final GaltonBoardDTO galtonBoardDTO, final GaltonBoard galtonBoard) {
        if (galtonBoardDTO.getDistribucion() != null) {
            Distribucion distribucion = galtonBoard.getDistribucion() != null ? galtonBoard.getDistribucion() : new Distribucion();
            distribucion.setDatos(galtonBoardDTO.getDistribucion().getDatos());
            distribucion.setNumBolas(galtonBoardDTO.getDistribucion().getNumBolas());
            distribucion.setNumContenedores(galtonBoardDTO.getDistribucion().getNumContenedores());

            distribucion = distribucionRepository.save(distribucion);
            galtonBoard.setDistribucion(distribucion);
        }
        return galtonBoard;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GaltonBoard crearYGuardarGaltonBoard(int index) {
        GaltonBoard galtonBoard = new GaltonBoard();
        galtonBoard.setNumBolas(500);
        galtonBoard.setNumContenedores(12);
        galtonBoard.setEstado("INICIALIZADO");

        Distribucion distribucion = new Distribucion();
        distribucion.setNumBolas(galtonBoard.getNumBolas());
        distribucion.setNumContenedores(galtonBoard.getNumContenedores());
        distribucion.setDatos(new HashMap<>());
        distribucion.setGaltonBoard(galtonBoard);

        galtonBoard.setDistribucion(distribucion);

        GaltonBoard savedGaltonBoard = galtonBoardRepository.save(galtonBoard);
        entityManager.flush();

        System.out.println("GaltonBoard creado y guardado con ID: " + savedGaltonBoard.getId());

        return savedGaltonBoard;
    }


    @Transactional(readOnly = true)
    public GaltonBoard getEntityById(Integer id) {
        return galtonBoardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + id));
    }
}


