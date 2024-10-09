package com.myproyect.springboot.services;

import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import com.myproyect.springboot.domain.distribution.Distribucion;
import com.myproyect.springboot.model.DistribucionDTO;
import com.myproyect.springboot.model.GaltonBoardDTO;
import com.myproyect.springboot.repos.DistribucionRepository;
import com.myproyect.springboot.repos.GaltonBoardRepository;
import com.myproyect.springboot.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class GaltonBoardService {

    @Autowired
    private DistribucionRepository distribucionRepository;

    private final GaltonBoardRepository galtonBoardRepository;

    @Autowired
    public GaltonBoardService(final GaltonBoardRepository galtonBoardRepository) {
        this.galtonBoardRepository = galtonBoardRepository;
    }

    // Metodo para iniciar la simulacion de la caida de bolas
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
                // Cada paso tiene una probabilidad del 50% de ir a la derecha.
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

    private Map<String, Integer> convertirAFormatoDistribucion(int[] contenedores) {
        Map<String, Integer> datos = new HashMap<>();
        for (int i = 0; i < contenedores.length; i++) {
            datos.put("Contenedor " + (i + 1), contenedores[i]); // Asegúrate de que cada contenedor tenga un conteo adecuado.
        }
        return datos;
    }


    // Metodo para actualizar la distribución de un GaltonBoard con una DistribucionDTO
    public void actualizarDistribucion(GaltonBoard galtonBoard, DistribucionDTO distribucionDTO) {
        // Asegurarse de que la distribución no sea null
        if (galtonBoard.getDistribucion() == null) {
            galtonBoard.setDistribucion(new Distribucion());
        }

        // Obtener la instancia de la distribución
        Distribucion distribucion = galtonBoard.getDistribucion();

        // Asegurarse de que los datos no sean null antes de asignarlos
        if (distribucionDTO.getDatos() != null) {
            distribucion.setDatos(distribucionDTO.getDatos());
        }
        distribucion.setNumBolas(distribucionDTO.getNumBolas());
        distribucion.setNumContenedores(distribucionDTO.getNumContenedores());

        // Guardar la distribución antes de guardar el GaltonBoard
        distribucion = distribucionRepository.save(distribucion);
        galtonBoard.setDistribucion(distribucion);
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
        // Asignar propiedades simples
        galtonBoard.setNumBolas(galtonBoardDTO.getNumBolas());
        galtonBoard.setNumContenedores(galtonBoardDTO.getNumContenedores());
        galtonBoard.setEstado(galtonBoardDTO.getEstado());

        // Asegurarse de que la distribución esté instanciada y guardada antes de asociarla
        if (galtonBoardDTO.getDistribucion() != null) {
            Distribucion distribucion = galtonBoard.getDistribucion() != null ? galtonBoard.getDistribucion() : new Distribucion();
            distribucion.setDatos(galtonBoardDTO.getDistribucion().getDatos());
            distribucion.setNumBolas(galtonBoardDTO.getDistribucion().getNumBolas());
            distribucion.setNumContenedores(galtonBoardDTO.getDistribucion().getNumContenedores());

            // Guardar la distribución antes de asociarla
            distribucion = distribucionRepository.save(distribucion);

            galtonBoard.setDistribucion(distribucion);
        }

        return galtonBoard;
    }

    public GaltonBoard getEntityById(final Integer id) {
        return galtonBoardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + id));
    }
}


