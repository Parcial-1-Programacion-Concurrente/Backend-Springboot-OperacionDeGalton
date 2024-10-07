package com.myproyect.springboot.model.maquinas;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaDistribucionNormalDTO extends MaquinaDTO {

    private double media;

    private double desviacionEstandar;

    private int maximoValor;

    private String estado;
}

