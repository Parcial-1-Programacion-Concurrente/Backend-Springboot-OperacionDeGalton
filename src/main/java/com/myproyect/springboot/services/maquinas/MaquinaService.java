package com.myproyect.springboot.services.maquinas;

import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.domain.factory.maquinas.*;
import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import com.myproyect.springboot.model.ComponenteDTO;
import com.myproyect.springboot.model.maquinas.*;
import com.myproyect.springboot.repos.ComponenteRepository;
import com.myproyect.springboot.repos.GaltonBoardRepository;
import com.myproyect.springboot.repos.maquinasRepos.*;
import com.myproyect.springboot.util.NotFoundException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public abstract class MaquinaService {

    @Autowired
    private GaltonBoardRepository galtonBoardRepository;

    @Getter
    private final MaquinaRepository maquinaRepository;

    @Autowired
    private MaquinaDistribucionPoissonRepository maquinaDistribucionPoissonRepository;

    @Autowired
    private MaquinaDistribucionBinomialRepository maquinaDistribucionBinomialRepository;

    @Autowired
    private MaquinaDistribucionGeometricaRepository maquinaDistribucionGeometricaRepository;

    @Autowired
    private MaquinaDistribucionExponencialRepository maquinaDistribucionExponencialRepository;

    @Autowired
    private MaquinaDistribucionNormalRepository maquinaDistribucionNormalRepository;

    @Autowired
    private MaquinaDistribucionUniformeRepository maquinaDistribucionUniformeRepository;

    @Autowired
    private MaquinaDistribucionCustomRepository maquinaDistribucionCustomRepository;


    private final ComponenteRepository componenteRepository;

    public MaquinaService(final MaquinaRepository maquinaRepository, ComponenteRepository componenteRepository) {
        this.componenteRepository = componenteRepository;
        this.maquinaRepository = maquinaRepository;
    }
    public List<MaquinaDTO> findAll() {
        return maquinaRepository.findAll(Sort.by("id")).stream()
                .map(this::mapMaquinaToSpecificDTO)
                .collect(Collectors.toList());
    }

    public MaquinaDTO get(final Integer id) {
        return maquinaRepository.findById(id)
                .map(this::mapMaquinaToSpecificDTO)
                .orElseThrow(NotFoundException::new);
    }

    public MaquinaDTO findById(final Integer id) {
        Maquina maquina = maquinaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Maquina no encontrada con id: " + id));

        return mapMaquinaToSpecificDTO(maquina);
    }

    @Transactional
    public Integer create(final MaquinaDTO maquinaDTO) {
        Maquina maquina;

        // Determinar la subclase a instanciar según el tipo especificado en el DTO
        switch (maquinaDTO.getTipo().toUpperCase()) {
            case "BINOMIAL":
                MaquinaDistribucionBinomialDTO binomialDTO = (MaquinaDistribucionBinomialDTO) maquinaDTO;
                MaquinaDistribucionBinomial maquinaDistribucionBinomial = new MaquinaDistribucionBinomial();
                maquinaDistribucionBinomial.setEstado(binomialDTO.getEstado());
                maquinaDistribucionBinomial.setNumeroComponentesRequeridos(binomialDTO.getNumeroComponentesRequeridos());
                maquinaDistribucionBinomial.setTipo(binomialDTO.getTipo());

                // Establecer el GaltonBoard
                GaltonBoard galtonBoardBinomial = galtonBoardRepository.findById(binomialDTO.getGaltonBoardId())
                        .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + binomialDTO.getGaltonBoardId()));
                maquinaDistribucionBinomial.setGaltonBoard(galtonBoardBinomial);

                maquinaDistribucionBinomial.setNumEnsayos(binomialDTO.getNumEnsayos());
                maquinaDistribucionBinomial.setProbabilidadExito(binomialDTO.getProbabilidadExito());

                maquina = maquinaDistribucionBinomial;
                break;

            case "GEOMETRICA":
                MaquinaDistribucionGeometricaDTO geometricaDTO = (MaquinaDistribucionGeometricaDTO) maquinaDTO;
                MaquinaDistribucionGeometrica maquinaDistribucionGeometrica = new MaquinaDistribucionGeometrica();
                maquinaDistribucionGeometrica.setEstado(geometricaDTO.getEstado());
                maquinaDistribucionGeometrica.setNumeroComponentesRequeridos(geometricaDTO.getNumeroComponentesRequeridos());
                maquinaDistribucionGeometrica.setTipo(geometricaDTO.getTipo());

                GaltonBoard galtonBoardGeometrica = galtonBoardRepository.findById(geometricaDTO.getGaltonBoardId())
                        .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + geometricaDTO.getGaltonBoardId()));
                maquinaDistribucionGeometrica.setGaltonBoard(galtonBoardGeometrica);

                maquinaDistribucionGeometrica.setProbabilidadExito(geometricaDTO.getProbabilidadExito());
                maquinaDistribucionGeometrica.setMaximoEnsayos(geometricaDTO.getMaximoEnsayos());

                maquina = maquinaDistribucionGeometrica;
                break;

            case "EXPONENCIAL":
                MaquinaDistribucionExponencialDTO exponencialDTO = (MaquinaDistribucionExponencialDTO) maquinaDTO;
                MaquinaDistribucionExponencial maquinaDistribucionExponencial = new MaquinaDistribucionExponencial();
                maquinaDistribucionExponencial.setEstado(exponencialDTO.getEstado());
                maquinaDistribucionExponencial.setNumeroComponentesRequeridos(exponencialDTO.getNumeroComponentesRequeridos());
                maquinaDistribucionExponencial.setTipo(exponencialDTO.getTipo());

                GaltonBoard galtonBoardExponencial = galtonBoardRepository.findById(exponencialDTO.getGaltonBoardId())
                        .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + exponencialDTO.getGaltonBoardId()));
                maquinaDistribucionExponencial.setGaltonBoard(galtonBoardExponencial);

                maquinaDistribucionExponencial.setLambda(exponencialDTO.getLambda());
                maquinaDistribucionExponencial.setMaximoValor(exponencialDTO.getMaximoValor());

                maquina = maquinaDistribucionExponencial;
                break;

            case "NORMAL":
                MaquinaDistribucionNormalDTO normalDTO = (MaquinaDistribucionNormalDTO) maquinaDTO;
                MaquinaDistribucionNormal maquinaDistribucionNormal = new MaquinaDistribucionNormal();
                maquinaDistribucionNormal.setEstado(normalDTO.getEstado());
                maquinaDistribucionNormal.setNumeroComponentesRequeridos(normalDTO.getNumeroComponentesRequeridos());
                maquinaDistribucionNormal.setTipo(normalDTO.getTipo());

                GaltonBoard galtonBoardNormal = galtonBoardRepository.findById(normalDTO.getGaltonBoardId())
                        .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + normalDTO.getGaltonBoardId()));
                maquinaDistribucionNormal.setGaltonBoard(galtonBoardNormal);

                maquinaDistribucionNormal.setMedia(normalDTO.getMedia());
                maquinaDistribucionNormal.setDesviacionEstandar(normalDTO.getDesviacionEstandar());
                maquinaDistribucionNormal.setMaximoValor(normalDTO.getMaximoValor());

                maquina = maquinaDistribucionNormal;
                break;

            case "UNIFORME":
                MaquinaDistribucionUniformeDTO uniformeDTO = (MaquinaDistribucionUniformeDTO) maquinaDTO;
                MaquinaDistribucionUniforme maquinaDistribucionUniforme = new MaquinaDistribucionUniforme();
                maquinaDistribucionUniforme.setEstado(uniformeDTO.getEstado());
                maquinaDistribucionUniforme.setNumeroComponentesRequeridos(uniformeDTO.getNumeroComponentesRequeridos());
                maquinaDistribucionUniforme.setTipo(uniformeDTO.getTipo());

                GaltonBoard galtonBoardUniforme = galtonBoardRepository.findById(uniformeDTO.getGaltonBoardId())
                        .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + uniformeDTO.getGaltonBoardId()));
                maquinaDistribucionUniforme.setGaltonBoard(galtonBoardUniforme);

                maquinaDistribucionUniforme.setNumValores(uniformeDTO.getNumValores());

                maquina = maquinaDistribucionUniforme;
                break;

            case "CUSTOM":
                MaquinaDistribucionCustomDTO customDTO = (MaquinaDistribucionCustomDTO) maquinaDTO;
                MaquinaDistribucionCustom maquinaDistribucionCustom = new MaquinaDistribucionCustom();
                maquinaDistribucionCustom.setEstado(customDTO.getEstado());
                maquinaDistribucionCustom.setNumeroComponentesRequeridos(customDTO.getNumeroComponentesRequeridos());
                maquinaDistribucionCustom.setTipo(customDTO.getTipo());

                GaltonBoard galtonBoardCustom = galtonBoardRepository.findById(customDTO.getGaltonBoardId())
                        .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + customDTO.getGaltonBoardId()));
                maquinaDistribucionCustom.setGaltonBoard(galtonBoardCustom);

                maquinaDistribucionCustom.setProbabilidadesPersonalizadas(customDTO.getProbabilidadesPersonalizadas());

                maquina = maquinaDistribucionCustom;
                break;

            case "POISSON":
                MaquinaDistribucionPoissonDTO poissonDTO = (MaquinaDistribucionPoissonDTO) maquinaDTO;
                MaquinaDistribucionPoisson maquinaDistribucionPoisson = new MaquinaDistribucionPoisson();
                maquinaDistribucionPoisson.setEstado(poissonDTO.getEstado());
                maquinaDistribucionPoisson.setNumeroComponentesRequeridos(poissonDTO.getNumeroComponentesRequeridos());
                maquinaDistribucionPoisson.setTipo(poissonDTO.getTipo());

                GaltonBoard galtonBoardPoisson = galtonBoardRepository.findById(poissonDTO.getGaltonBoardId())
                        .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + poissonDTO.getGaltonBoardId()));
                maquinaDistribucionPoisson.setGaltonBoard(galtonBoardPoisson);

                maquinaDistribucionPoisson.setLambda(poissonDTO.getLambda());
                maquinaDistribucionPoisson.setMaximoValor(poissonDTO.getMaximoValor());

                maquina = maquinaDistribucionPoisson;
                break;

            default:
                throw new IllegalArgumentException("Tipo de máquina desconocido: " + maquinaDTO.getTipo());
        }

        // Guardar la máquina usando el repositorio específico
        Maquina savedMaquina = saveMaquinaByType(maquina);
        return savedMaquina.getId();
    }

    // Metodo para guardar la máquina usando el repositorio específico
    private Maquina saveMaquinaByType(Maquina maquina) {
        if (maquina instanceof MaquinaDistribucionBinomial binomial) {
            return maquinaDistribucionBinomialRepository.save(binomial);
        } else if (maquina instanceof MaquinaDistribucionGeometrica geometrica) {
            return maquinaDistribucionGeometricaRepository.save(geometrica);
        } else if (maquina instanceof MaquinaDistribucionExponencial exponencial) {
            return maquinaDistribucionExponencialRepository.save(exponencial);
        } else if (maquina instanceof MaquinaDistribucionNormal normal) {
            return maquinaDistribucionNormalRepository.save(normal);
        } else if (maquina instanceof MaquinaDistribucionUniforme uniforme) {
            return maquinaDistribucionUniformeRepository.save(uniforme);
        } else if (maquina instanceof MaquinaDistribucionCustom custom) {
            return maquinaDistribucionCustomRepository.save(custom);
        } else if (maquina instanceof MaquinaDistribucionPoisson poisson) {
            return maquinaDistribucionPoissonRepository.save(poisson);
        } else {
            throw new IllegalArgumentException("Tipo de máquina desconocido al guardar");
        }
    }


    private MaquinaDTO mapMaquinaToSpecificDTO(Maquina maquina) {
        if (maquina instanceof MaquinaDistribucionBinomial binomial) {
            MaquinaDistribucionBinomialDTO dto = new MaquinaDistribucionBinomialDTO();
            dto.setId(binomial.getId());
            dto.setEstado(binomial.getEstado());
            dto.setNumEnsayos(binomial.getNumEnsayos());
            dto.setProbabilidadExito(binomial.getProbabilidadExito());
            return dto;
        } else if (maquina instanceof MaquinaDistribucionGeometrica geometrica) {
            MaquinaDistribucionGeometricaDTO dto = new MaquinaDistribucionGeometricaDTO();
            dto.setId(geometrica.getId());
            dto.setEstado(geometrica.getEstado());
            dto.setProbabilidadExito(geometrica.getProbabilidadExito());
            dto.setMaximoEnsayos(geometrica.getMaximoEnsayos());
            return dto;
        } else if (maquina instanceof MaquinaDistribucionExponencial exponencial) {
            MaquinaDistribucionExponencialDTO dto = new MaquinaDistribucionExponencialDTO();
            dto.setId(exponencial.getId());
            dto.setEstado(exponencial.getEstado());
            dto.setLambda(exponencial.getLambda());
            dto.setMaximoValor(exponencial.getMaximoValor());
            return dto;
        } else if (maquina instanceof MaquinaDistribucionPoisson poisson) {
            MaquinaDistribucionPoissonDTO dto = new MaquinaDistribucionPoissonDTO();
            dto.setId(poisson.getId());
            dto.setEstado(poisson.getEstado());
            dto.setLambda(poisson.getLambda());
            dto.setMaximoValor(poisson.getMaximoValor());
            return dto;
        } else if (maquina instanceof MaquinaDistribucionUniforme uniforme) {
            MaquinaDistribucionUniformeDTO dto = new MaquinaDistribucionUniformeDTO();
            dto.setId(uniforme.getId());
            dto.setEstado(uniforme.getEstado());
            dto.setNumValores(uniforme.getNumValores());
            return dto;
        } else if (maquina instanceof MaquinaDistribucionCustom custom) {
            MaquinaDistribucionCustomDTO dto = new MaquinaDistribucionCustomDTO();
            dto.setId(custom.getId());
            dto.setEstado(custom.getEstado());
            dto.setProbabilidadesPersonalizadas(custom.getProbabilidadesPersonalizadas());
            return dto;
        } else if (maquina instanceof MaquinaDistribucionNormal normal) {
            MaquinaDistribucionNormalDTO dto = new MaquinaDistribucionNormalDTO();
            dto.setId(normal.getId());
            dto.setEstado(normal.getEstado());
            dto.setMedia(normal.getMedia());
            dto.setDesviacionEstandar(normal.getDesviacionEstandar());
            dto.setMaximoValor(normal.getMaximoValor());
            return dto;
        } else {
            throw new IllegalArgumentException("Tipo de máquina desconocido");
        }
    }

    public void update(final Integer id, final MaquinaDTO maquinaDTO) {
        Maquina maquina = maquinaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(maquinaDTO, maquina);
        maquinaRepository.save(maquina);
    }

    public void delete(final Integer id) {
        maquinaRepository.deleteById(id);
    }

    private MaquinaDTO mapToDTO(final Maquina maquina, final MaquinaDTO maquinaDTO) {
        // Mapear los atributos comunes de Maquina a MaquinaDTO
        maquinaDTO.setId(maquina.getId());
        maquinaDTO.setTipo(maquina.getTipo());

        // Mapear la lista de componentes a ComponenteDTO
        List<ComponenteDTO> componenteDTOs = maquina.getComponentes().stream()
                .map(componente -> {
                    ComponenteDTO componenteDTO = new ComponenteDTO();
                    componenteDTO.setId(componente.getId());
                    componenteDTO.setTipo(componente.getTipo());
                    componenteDTO.setValorCalculado(componente.getValorCalculado());
                    return componenteDTO;
                })
                .collect(Collectors.toList());
        maquinaDTO.setComponentes(componenteDTOs);

        return maquinaDTO;
    }


    public Maquina mapToEntity(final MaquinaDTO maquinaDTO, final Maquina maquina) {
        // Mapear los atributos comunes de MaquinaDTO a Maquina
        maquina.setTipo(maquinaDTO.getTipo());
        maquina.setEstado(maquinaDTO.getEstado());
        maquina.setGaltonBoard(maquinaDTO.getGaltonBoardId() != null
                ? galtonBoardRepository.findById(maquinaDTO.getGaltonBoardId())
                .orElseThrow(() -> new NotFoundException("GaltonBoard no encontrado con ID: " + maquinaDTO.getGaltonBoardId()))
                : null);

        // Mapear la lista de ComponenteDTO a Componente
        List<Componente> componentes = maquinaDTO.getComponentes().stream()
                .map(componenteDTO -> {
                    Componente componente = new Componente();
                    componente.setId(componenteDTO.getId());
                    componente.setTipo(componenteDTO.getTipo());
                    componente.setValorCalculado(componenteDTO.getValorCalculado());
                    return componente;
                })
                .collect(Collectors.toList());
        maquina.setComponentes(componentes);

        return maquina;
    }

    public boolean ensamblar(Integer maquinaId, List<ComponenteDTO> componentesDTO) {
        // Recuperar la máquina por su ID
        Maquina maquina = maquinaRepository.findById(maquinaId)
                .orElseThrow(() -> new NotFoundException("Máquina no encontrada con ID: " + maquinaId));

        // Convertir los DTOs a entidades de componentes
        List<Componente> componentes = componentesDTO.stream()
                .map(dto -> {
                    Componente componente = new Componente();
                    componente.setId(dto.getId());
                    componente.setTipo(dto.getTipo());
                    componente.setValorCalculado(dto.getValorCalculado());
                    componente.setMaquina(maquina); // Asignar la máquina al componente
                    return componente;
                })
                .collect(Collectors.toList());

        // Validar los componentes antes de ensamblar
        if (!validarComponentes(maquina, componentes)) {
            System.out.println("Los componentes no son válidos para el ensamblaje.");
            return false;
        }

        // Asignar los componentes a la máquina
        maquina.setComponentes(componentes);

        // Guardar la máquina y los componentes
        maquinaRepository.save(maquina);

        System.out.println("La máquina ha sido ensamblada con éxito.");
        return true;
    }


    // Metodo para validar los componentes antes del ensamblaje
    public boolean validarComponentes(Maquina maquina, List<Componente> componentes) {
        // Validación básica: Verificar que la lista de componentes no esté vacía
        if (componentes == null || componentes.isEmpty()) {
            System.out.println("La lista de componentes está vacía.");
            return false;
        }

        // Validar que todos los componentes tengan valores válidos
        for (Componente componente : componentes) {
            if (componente.getValorCalculado() <= 0) {
                System.out.println("El valor de un componente no es válido: " + componente.getTipo());
                return false;
            }
        }

        // Validaciones específicas según el tipo de máquina
        if (maquina instanceof MaquinaDistribucionBinomial) {
            return validarComponentesBinomial(componentes);
        } else if (maquina instanceof MaquinaDistribucionGeometrica) {
            return validarComponentesGeometrica(componentes);
        } else if (maquina instanceof MaquinaDistribucionExponencial) {
            return validarComponentesExponencial(componentes);
        } else if (maquina instanceof MaquinaDistribucionNormal) {
            return validarComponentesNormal(componentes);
        } else if (maquina instanceof MaquinaDistribucionUniforme) {
            return validarComponentesUniforme(componentes);
        } else if (maquina instanceof MaquinaDistribucionCustom) {
            return validarComponentesCustom(componentes);
        } else if (maquina instanceof MaquinaDistribucionPoisson) {
            return validarComponentesPoisson(componentes);
        }

        // Si la máquina no coincide con ningún tipo conocido, la validación falla
        System.out.println("El tipo de máquina no es compatible con la validación de componentes.");
        return false;
    }
    // Validación específica para MaquinaDistribucionBinomial
    private boolean validarComponentesBinomial(List<Componente> componentes) {
        long tipo1Count = componentes.stream().filter(c -> c.getTipo().equals("Tipo1")).count();
        long tipo2Count = componentes.stream().filter(c -> c.getTipo().equals("Tipo2")).count();
        if (tipo1Count < 1 || tipo2Count < 1) {
            System.out.println("MaquinaDistribucionBinomial necesita al menos un componente de Tipo1 y uno de Tipo2.");
            return false;
        }
        return true;
    }

    // Validación específica para MaquinaDistribucionGeometrica
    private boolean validarComponentesGeometrica(List<Componente> componentes) {
        // Verificar que todos los componentes tengan un valor en un rango específico
        boolean valid = componentes.stream().allMatch(c -> c.getValorCalculado() > 0 && c.getValorCalculado() <= 1);
        if (!valid) {
            System.out.println("Los componentes de MaquinaDistribucionGeometrica deben tener valores entre 0 y 1.");
            return false;
        }
        return true;
    }

    // Validación específica para MaquinaDistribucionExponencial
    private boolean validarComponentesExponencial(List<Componente> componentes) {
        // Verificar que la suma de los valores calculados no sea negativa
        double sumaValores = componentes.stream().mapToDouble(Componente::getValorCalculado).sum();
        if (sumaValores <= 0) {
            System.out.println("La suma de los valores de los componentes de MaquinaDistribucionExponencial debe ser positiva.");
            return false;
        }
        return true;
    }

    // Validación específica para MaquinaDistribucionNormal
    private boolean validarComponentesNormal(List<Componente> componentes) {
        // Validar que haya una cantidad suficiente de componentes para la distribución normal
        if (componentes.size() < 10) {
            System.out.println("MaquinaDistribucionNormal necesita al menos 10 componentes para una distribución adecuada.");
            return false;
        }
        return true;
    }

    // Validación específica para MaquinaDistribucionUniforme
    private boolean validarComponentesUniforme(List<Componente> componentes) {
        // Verificar que todos los componentes tengan el mismo tipo para una distribución uniforme
        String tipo = componentes.get(0).getTipo();
        boolean sameType = componentes.stream().allMatch(c -> c.getTipo().equals(tipo));
        if (!sameType) {
            System.out.println("Todos los componentes de MaquinaDistribucionUniforme deben ser del mismo tipo.");
            return false;
        }
        return true;
    }

    // Validación específica para MaquinaDistribucionCustom
    private boolean validarComponentesCustom(List<Componente> componentes) {
        // Validar que cada componente tenga una probabilidad personalizada asignada
        boolean allHaveProbabilities = componentes.stream()
                .allMatch(c -> c.getValorCalculado() >= 0 && c.getValorCalculado() <= 1);
        if (!allHaveProbabilities) {
            System.out.println("Los componentes de MaquinaDistribucionCustom deben tener probabilidades entre 0 y 1.");
            return false;
        }
        return true;
    }

    // Validación específica para MaquinaDistribucionPoisson
    private boolean validarComponentesPoisson(List<Componente> componentes) {
        // Verificar que haya un solo componente con un valor específico para lambda
        long lambdaComponents = componentes.stream().filter(c -> c.getTipo().equals("Lambda")).count();
        if (lambdaComponents != 1) {
            System.out.println("MaquinaDistribucionPoisson necesita exactamente un componente de tipo 'Lambda'.");
            return false;
        }
        return true;
    }

    public Maquina ToEntity(final MaquinaDTO maquinaDTO) {
        Maquina maquina;

        switch (maquinaDTO.getTipo().toUpperCase()) {
            case "BINOMIAL":
                maquina = new MaquinaDistribucionBinomial();
                break;
            case "GEOMETRICA":
                maquina = new MaquinaDistribucionGeometrica();
                break;
            case "EXPONENCIAL":
                maquina = new MaquinaDistribucionExponencial();
                break;
            case "NORMAL":
                maquina = new MaquinaDistribucionNormal();
                break;
            case "UNIFORME":
                maquina = new MaquinaDistribucionUniforme();
                break;
            case "CUSTOM":
                maquina = new MaquinaDistribucionCustom();
                break;
            case "POISSON":
                maquina = new MaquinaDistribucionPoisson();
                break;
            default:
                throw new IllegalArgumentException("Tipo de máquina desconocido: " + maquinaDTO.getTipo());
        }

        maquina.setTipo(maquinaDTO.getTipo());
        maquina.setEstado(maquinaDTO.getEstado());


        return maquina;
    }


    public abstract Map<String, Integer> calcularDistribucion(Integer id);
}

