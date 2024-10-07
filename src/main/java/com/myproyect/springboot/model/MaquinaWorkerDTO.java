package com.myproyect.springboot.model;



import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MaquinaWorkerDTO {

    private Integer id;

    @NotNull
    private MaquinaDTO maquina;

    private List<ComponenteWorkerDTO> componenteWorkers;
}
