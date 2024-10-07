package com.myproyect.springboot.model.maquinas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaDistribucionBinomialDTO extends MaquinaDTO {

    private int numEnsayos;

    private double probabilidadExito;

    private String estado;

}
