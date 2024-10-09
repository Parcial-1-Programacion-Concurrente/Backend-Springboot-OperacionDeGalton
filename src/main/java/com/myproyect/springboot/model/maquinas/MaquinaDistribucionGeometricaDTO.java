package com.myproyect.springboot.model.maquinas;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaDistribucionGeometricaDTO extends MaquinaDTO {

    private double probabilidadExito;

    private int maximoEnsayos;

    @NotNull
    private String estado;

    private Integer galtonBoardId;

}

