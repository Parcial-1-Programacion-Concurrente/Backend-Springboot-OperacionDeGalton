package com.myproyect.springboot.model.maquinas;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class MaquinaDistribucionCustomDTO extends MaquinaDTO {

    private Map<String, Integer> probabilidadesPersonalizadas;

    private String estado;
}

