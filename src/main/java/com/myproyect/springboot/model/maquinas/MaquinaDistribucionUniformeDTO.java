package com.myproyect.springboot.model.maquinas;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaDistribucionUniformeDTO extends MaquinaDTO {

    private int numValores;

    @NotNull
    private String estado;
}
