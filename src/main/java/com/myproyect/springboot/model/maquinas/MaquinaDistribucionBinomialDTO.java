package com.myproyect.springboot.model.maquinas;

import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaDistribucionBinomialDTO extends MaquinaDTO {

    private int numEnsayos;

    private double probabilidadExito;

    @NotNull
    private String estado;

}
