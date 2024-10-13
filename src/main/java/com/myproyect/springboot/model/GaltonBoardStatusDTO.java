package com.myproyect.springboot.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class GaltonBoardStatusDTO {

    private Integer id;

    @NotNull
    private String estado;

    @NotNull
    private Map<String, Integer> distribucionActual;

}
