package com.myproyect.springboot.model;



import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MaquinaFactoryDTO {

    @NotNull
    private List<ComponenteDTO> componentes;

    private MaquinaDTO maquina;
}
