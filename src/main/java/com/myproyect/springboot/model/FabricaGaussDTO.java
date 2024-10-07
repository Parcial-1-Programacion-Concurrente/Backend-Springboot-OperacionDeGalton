package com.myproyect.springboot.model;



import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FabricaGaussDTO {

    private Integer id;

    @NotNull
    private List<EstacionTrabajoDTO> estaciones;

    private LineaEnsamblajeDTO lineaEnsamblaje;

    @NotNull
    private Integer numEstaciones;
}
