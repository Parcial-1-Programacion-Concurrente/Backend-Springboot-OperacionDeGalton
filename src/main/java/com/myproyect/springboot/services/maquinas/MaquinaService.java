package com.myproyect.springboot.services.maquinas;

import com.myproyect.springboot.domain.concurrency.Componente;
import com.myproyect.springboot.domain.factory.maquinas.*;
import com.myproyect.springboot.model.ComponenteDTO;
import com.myproyect.springboot.model.maquinas.*;
import com.myproyect.springboot.repos.ComponenteRepository;
import com.myproyect.springboot.repos.MaquinaRepository;
import com.myproyect.springboot.util.NotFoundException;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public abstract class MaquinaService {


    @Getter
    private final MaquinaRepository maquinaRepository;

    private final ComponenteRepository componenteRepository;

    public MaquinaService(final MaquinaRepository maquinaRepository, final ComponenteRepository componenteRepository) {
        this.maquinaRepository = maquinaRepository;
        this.componenteRepository = componenteRepository;
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

    public Integer create(final MaquinaDTO maquinaDTO) {
        Maquina maquina;

        // Determinar la subclase a instanciar según el tipo especificado en el DTO
        switch (maquinaDTO.getTipo()) {
            case "BINOMIAL":
                MaquinaDistribucionBinomialDTO binomialDTO = (MaquinaDistribucionBinomialDTO) maquinaDTO;
                maquina = new MaquinaDistribucionBinomial();
                ((MaquinaDistribucionBinomial) maquina).setNumEnsayos(binomialDTO.getNumEnsayos());
                ((MaquinaDistribucionBinomial) maquina).setProbabilidadExito(binomialDTO.getProbabilidadExito());
                break;

            case "GEOMETRICA":
                MaquinaDistribucionGeometricaDTO geometricaDTO = (MaquinaDistribucionGeometricaDTO) maquinaDTO;
                maquina = new MaquinaDistribucionGeometrica();
                ((MaquinaDistribucionGeometrica) maquina).setProbabilidadExito(geometricaDTO.getProbabilidadExito());
                ((MaquinaDistribucionGeometrica) maquina).setMaximoEnsayos(geometricaDTO.getMaximoEnsayos());
                break;

            case "EXPONENCIAL":
                MaquinaDistribucionExponencialDTO exponencialDTO = (MaquinaDistribucionExponencialDTO) maquinaDTO;
                maquina = new MaquinaDistribucionExponencial();
                ((MaquinaDistribucionExponencial) maquina).setLambda(exponencialDTO.getLambda());
                ((MaquinaDistribucionExponencial) maquina).setMaximoValor(exponencialDTO.getMaximoValor());
                break;

            case "NORMAL":
                MaquinaDistribucionNormalDTO normalDTO = (MaquinaDistribucionNormalDTO) maquinaDTO;
                maquina = new MaquinaDistribucionNormal();
                ((MaquinaDistribucionNormal) maquina).setMedia(normalDTO.getMedia());
                ((MaquinaDistribucionNormal) maquina).setDesviacionEstandar(normalDTO.getDesviacionEstandar());
                ((MaquinaDistribucionNormal) maquina).setMaximoValor(normalDTO.getMaximoValor());
                break;

            case "UNIFORME":
                MaquinaDistribucionUniformeDTO uniformeDTO = (MaquinaDistribucionUniformeDTO) maquinaDTO;
                maquina = new MaquinaDistribucionUniforme();
                ((MaquinaDistribucionUniforme) maquina).setNumValores(uniformeDTO.getNumValores());
                break;

            case "CUSTOM":
                MaquinaDistribucionCustomDTO customDTO = (MaquinaDistribucionCustomDTO) maquinaDTO;
                maquina = new MaquinaDistribucionCustom();
                ((MaquinaDistribucionCustom) maquina).setProbabilidadesPersonalizadas(customDTO.getProbabilidadesPersonalizadas());
                break;

            case "POISSON":
                MaquinaDistribucionPoissonDTO poissonDTO = (MaquinaDistribucionPoissonDTO) maquinaDTO;
                maquina = new MaquinaDistribucionPoisson();
                ((MaquinaDistribucionPoisson) maquina).setLambda(poissonDTO.getLambda());
                ((MaquinaDistribucionPoisson) maquina).setMaximoValor(poissonDTO.getMaximoValor());
                break;

            default:
                throw new IllegalArgumentException("Tipo de máquina desconocido: " + maquinaDTO.getTipo());
        }

        mapToEntity(maquinaDTO, maquina);
        return maquinaRepository.save(maquina).getId();
    }

    private MaquinaDTO mapMaquinaToSpecificDTO(Maquina maquina) {
        if (maquina instanceof MaquinaDistribucionBinomial) {
            MaquinaDistribucionBinomial binomial = (MaquinaDistribucionBinomial) maquina;
            MaquinaDistribucionBinomialDTO dto = new MaquinaDistribucionBinomialDTO();
            dto.setId(binomial.getId());
            dto.setEstado(binomial.getEstado());
            dto.setNumEnsayos(binomial.getNumEnsayos());
            dto.setProbabilidadExito(binomial.getProbabilidadExito());
            return dto;
        } else if (maquina instanceof MaquinaDistribucionGeometrica) {
            MaquinaDistribucionGeometrica geometrica = (MaquinaDistribucionGeometrica) maquina;
            MaquinaDistribucionGeometricaDTO dto = new MaquinaDistribucionGeometricaDTO();
            dto.setId(geometrica.getId());
            dto.setEstado(geometrica.getEstado());
            dto.setProbabilidadExito(geometrica.getProbabilidadExito());
            dto.setMaximoEnsayos(geometrica.getMaximoEnsayos());
            return dto;
        } else if (maquina instanceof MaquinaDistribucionExponencial) {
            MaquinaDistribucionExponencial exponencial = (MaquinaDistribucionExponencial) maquina;
            MaquinaDistribucionExponencialDTO dto = new MaquinaDistribucionExponencialDTO();
            dto.setId(exponencial.getId());
            dto.setEstado(exponencial.getEstado());
            dto.setLambda(exponencial.getLambda());
            dto.setMaximoValor(exponencial.getMaximoValor());
            return dto;
        } else if (maquina instanceof MaquinaDistribucionPoisson) {
            MaquinaDistribucionPoisson poisson = (MaquinaDistribucionPoisson) maquina;
            MaquinaDistribucionPoissonDTO dto = new MaquinaDistribucionPoissonDTO();
            dto.setId(poisson.getId());
            dto.setEstado(poisson.getEstado());
            dto.setLambda(poisson.getLambda());
            dto.setMaximoValor(poisson.getMaximoValor());
            return dto;
        } else if (maquina instanceof MaquinaDistribucionUniforme) {
            MaquinaDistribucionUniforme uniforme = (MaquinaDistribucionUniforme) maquina;
            MaquinaDistribucionUniformeDTO dto = new MaquinaDistribucionUniformeDTO();
            dto.setId(uniforme.getId());
            dto.setEstado(uniforme.getEstado());
            dto.setNumValores(uniforme.getNumValores());
            return dto;
        } else if (maquina instanceof MaquinaDistribucionCustom) {
            MaquinaDistribucionCustom custom = (MaquinaDistribucionCustom) maquina;
            MaquinaDistribucionCustomDTO dto = new MaquinaDistribucionCustomDTO();
            dto.setId(custom.getId());
            dto.setEstado(custom.getEstado());
            dto.setProbabilidadesPersonalizadas(custom.getProbabilidadesPersonalizadas());
            return dto;
        } else if (maquina instanceof MaquinaDistribucionNormal) {
            MaquinaDistribucionNormal normal = (MaquinaDistribucionNormal) maquina;
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


    private Maquina mapToEntity(final MaquinaDTO maquinaDTO, final Maquina maquina) {
        // Mapear los atributos comunes de MaquinaDTO a Maquina
        maquina.setTipo(maquinaDTO.getTipo());

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

    // Metodo para ensamblar una máquina con una lista de componentes
    public boolean ensamblar(Integer maquinaId, List<ComponenteDTO> componentesDTO) {
        // Recuperar la máquina por su ID
        Maquina maquina = maquinaRepository.findById(maquinaId)
                .orElseThrow(() -> new NotFoundException("Maquina no encontrada con id: " + maquinaId));

        // Convertir los DTOs a entidades de componentes
        List<Componente> componentes = componentesDTO.stream()
                .map(dto -> {
                    Componente componente = new Componente();
                    componente.setId(dto.getId());
                    componente.setTipo(dto.getTipo());
                    componente.setValorCalculado(dto.getValorCalculado());
                    return componente;
                })
                .collect(Collectors.toList());

        // Validar los componentes antes de ensamblar
        if (!validarComponentes(maquina, componentes)) {
            System.out.println("Los componentes no son válidos para el ensamblaje.");
            return false;
        }

        // Asignar los componentes a la máquina si son válidos y guardar en el repositorio
        maquina.setComponentes(componentes);
        maquinaRepository.save(maquina);

        // Guardar los componentes actualizados en el repositorio
        componenteRepository.saveAll(componentes);

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
        // Verificar que haya al menos dos tipos diferentes de componentes, por ejemplo
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


    public abstract Map<String, Integer> calcularDistribucion(Integer id);
}

