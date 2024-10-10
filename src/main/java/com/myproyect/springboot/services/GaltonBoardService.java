package com.myproyect.springboot.services;

import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import com.myproyect.springboot.domain.distribution.Distribucion;
import com.myproyect.springboot.model.DistribucionDTO;
import com.myproyect.springboot.model.GaltonBoardDTO;
import com.myproyect.springboot.repos.DistribucionRepository;
import com.myproyect.springboot.repos.GaltonBoardRepository;
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

    private final GaltonBoardRepository galtonBoardRepository;

    @Autowired
    public GaltonBoardService(final GaltonBoardRepository galtonBoardRepository) {
        this.galtonBoardRepository = galtonBoardRepository;
    }

    // Metodo para iniciar la simulación de la caída de bolas
    public void simularCaidaDeBolas(Integer galtonBoardId) {
        GaltonBoard galtonBoard = galtonBoardRepository.findById(galtonBoardId)
                .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + galtonBoardId));

        // Inicializar el estado de la simulación.
        galtonBoard.setEstado("EN_SIMULACION");

        // Simulación de la caída de bolas.
        int[] contenedores = new int[galtonBoard.getNumContenedores()];
        Random random = new Random();
        for (int i = 0; i < galtonBoard.getNumBolas(); i++) {
            int contenedorSeleccionado = 0;
            for (int j = 0; j < galtonBoard.getNumContenedores() - 1; j++) {
                if (random.nextBoolean()) {
                    contenedorSeleccionado++;
                }
            }
                contenedores[contenedorSeleccionado]++;

        }

        // Guardar la distribución en la entidad Distribucion.
        Distribucion distribucion = galtonBoard.getDistribucion();
        if (distribucion == null) {
            distribucion = new Distribucion();
            galtonBoard.setDistribucion(distribucion);
        }
        distribucion.setDatos(convertirAFormatoDistribucion(contenedores));
        distribucion.setNumBolas(galtonBoard.getNumBolas());
        distribucion.setNumContenedores(galtonBoard.getNumContenedores());
        distribucion.setGaltonBoard(galtonBoard);

        // Guardar la distribución antes de guardar el GaltonBoard.
        distribucion = distribucionRepository.save(distribucion);
        galtonBoard.setDistribucion(distribucion);

        // Marcar la simulación como finalizada y guardar el estado actualizado.
        galtonBoard.setEstado("FINALIZADA");
        galtonBoardRepository.save(galtonBoard);

        System.out.println("Simulación de caída de bolas finalizada para el GaltonBoard con ID: " + galtonBoardId);
    }

    // Metodo auxiliar para convertir la distribución a un formato de Map<String, Integer>
    private Map<String, Integer> convertirAFormatoDistribucion(int[] contenedores) {
        Map<String, Integer> distribucionMap = new HashMap<>();
        for (int i = 0; i < contenedores.length; i++) {
            distribucionMap.put("Contenedor " + i, contenedores[i]);
        }
        return distribucionMap;
    }


    // Metodo para actualizar la distribución de un GaltonBoard con una DistribucionDTO
    @Transactional
    public void actualizarDistribucion(GaltonBoard galtonBoard, DistribucionDTO distribucionDTO) {

        System.out.println("Actualizando distribución para GaltonBoard con ID: " + galtonBoard.getId());

        // Verificar si el GaltonBoard está persistido
        if (galtonBoard.getId() == null) {
            galtonBoard = galtonBoardRepository.save(galtonBoard);
            System.out.println("GaltonBoard guardado con ID: " + galtonBoard.getId());
        } else {
            // Asignar el ID a una variable final
            final Integer galtonBoardId = galtonBoard.getId();

            // Recargar el GaltonBoard desde la base de datos
            galtonBoard = galtonBoardRepository.findById(galtonBoardId)
                    .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + galtonBoardId));
        }

        // Obtener la instancia de la distribución
        Distribucion distribucion = galtonBoard.getDistribucion();

        if (distribucion == null) {
            // Crear nueva Distribucion y asociarla al GaltonBoard
            distribucion = new Distribucion();
            distribucion.setGaltonBoard(galtonBoard);
            galtonBoard.setDistribucion(distribucion);
        } else {
            // Asignar el ID a una variable final
            final Integer distribucionId = distribucion.getId();

            // Recargar la Distribucion desde la base de datos
            distribucion = distribucionRepository.findById(distribucionId)
                    .orElseThrow(() -> new NotFoundException("Distribucion no encontrada con ID: " + distribucionId));
        }

        // Actualizar los datos de la distribución
        if (distribucionDTO.getDatos() != null) {
            distribucion.setDatos(distribucionDTO.getDatos());
        }
        distribucion.setNumBolas(distribucionDTO.getNumBolas());
        distribucion.setNumContenedores(distribucionDTO.getNumContenedores());

        // Guardar la distribución
        distribucion = distribucionRepository.save(distribucion);

        System.out.println("Distribución actualizada para GaltonBoard con ID: " + galtonBoard.getId());
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
        galtonBoard.setNumBolas(10 + (index * 10));
        galtonBoard.setNumContenedores(3);  // Asegúrate de que este número sea el adecuado
        galtonBoard.setEstado("INICIALIZADO");

        // Crea solo una distribución por cada GaltonBoard.
        Distribucion distribucion = new Distribucion();
        distribucion.setNumBolas(galtonBoard.getNumBolas());
        distribucion.setNumContenedores(galtonBoard.getNumContenedores());
        distribucion.setDatos(new HashMap<>());
        distribucion.setGaltonBoard(galtonBoard);

        galtonBoard.setDistribucion(distribucion);

        // Guardar GaltonBoard y Distribución juntos
        GaltonBoard savedGaltonBoard = galtonBoardRepository.save(galtonBoard);
        entityManager.flush();

        System.out.println("GaltonBoard creado y guardado con ID: " + savedGaltonBoard.getId());

        return savedGaltonBoard;
    }


    public void mostrarGaltonBoardCompacto(GaltonBoard galtonBoard) {
        Distribucion distribucion = galtonBoard.getDistribucion();
        if (distribucion == null || distribucion.getDatos().isEmpty()) {
            System.out.println("La distribución está vacía para el GaltonBoard con ID: " + galtonBoard.getId());
            return;
        }

        System.out.println("Distribución del GaltonBoard con ID: " + galtonBoard.getId() + ":");

        // Obtener los datos de la distribución
        Map<String, Integer> datos = distribucion.getDatos();

        // Determinar el valor máximo para normalizar las barras
        int maxBolas = datos.values().stream().max(Integer::compareTo).orElse(1);

        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            String contenedor = entry.getKey();
            int bolas = entry.getValue();

            // Normalizar la cantidad de bolas para que la longitud de la barra no exceda un límite visual, por ejemplo, 50 caracteres
            int longitudBarra = (int) ((bolas / (double) maxBolas) * 50);

            // Construir la barra utilizando un carácter visual como "█"
            String barra = "█".repeat(longitudBarra);

            System.out.printf("%-10s: %-50s (%d)%n", contenedor, barra, bolas);
        }
    }

    @Transactional(readOnly = true)
    public GaltonBoard getEntityById(Integer id) {
        return galtonBoardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + id));
    }
}


