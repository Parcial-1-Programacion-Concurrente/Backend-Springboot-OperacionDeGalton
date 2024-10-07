package com.myproyect.springboot.model.maquinas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaDistribucionGeometricaDTO extends MaquinaDTO {

    private double probabilidadExito;

    private int maximoEnsayos;

    private String estado;

}

