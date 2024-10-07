package com.myproyect.springboot.model;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class MaquinaWorkerDTO {

    private Long id;

    @NotNull
    private Long maquinaId;

    private List<ComponenteWorkerDTO> componenteWorkers;
}
