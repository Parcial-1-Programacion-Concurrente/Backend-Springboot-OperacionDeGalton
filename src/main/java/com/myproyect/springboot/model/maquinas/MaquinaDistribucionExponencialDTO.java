package com.myproyect.springboot.model.maquinas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaDistribucionExponencialDTO extends MaquinaDTO {

    private double lambda;

    private int maximoValor;

    private String estado;

}

