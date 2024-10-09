package com.myproyect.springboot.model.maquinas;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class MaquinaDistribucionCustomDTO extends MaquinaDTO {

    private Map<String, Integer> probabilidadesPersonalizadas;

    @NotNull
    private String estado;

    private Integer galtonBoardId;
}

