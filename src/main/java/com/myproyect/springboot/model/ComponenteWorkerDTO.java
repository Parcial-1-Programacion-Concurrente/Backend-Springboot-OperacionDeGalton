package com.myproyect.springboot.model;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComponenteWorkerDTO {

    private Integer id;

    @NotNull
    private ComponenteDTO componente;

    @NotNull
    private GaltonBoardDTO galtonBoard;
}
