package com.myproyect.springboot.model.maquinas;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaDistribucionExponencialDTO extends MaquinaDTO {

    private double lambda;

    private int maximoValor;

    @NotNull
    private String estado;

}

