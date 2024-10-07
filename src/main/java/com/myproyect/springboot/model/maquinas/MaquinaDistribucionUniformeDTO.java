package com.myproyect.springboot.model.maquinas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaDistribucionUniformeDTO extends MaquinaDTO {

    private int numValores;


    private String estado;
}
