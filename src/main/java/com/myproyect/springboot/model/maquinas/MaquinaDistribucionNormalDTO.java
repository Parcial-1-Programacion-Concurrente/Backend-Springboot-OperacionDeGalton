package com.myproyect.springboot.model.maquinas;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaDistribucionNormalDTO extends MaquinaDTO {

    private double media;

    private double desviacionEstandar;

    private int maximoValor;

    @NotNull
    private String estado;

    private Integer galtonBoardId;
}

