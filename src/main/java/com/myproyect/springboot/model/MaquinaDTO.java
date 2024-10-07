package com.myproyect.springboot.model;



import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MaquinaDTO {

    private Integer id;

    @NotNull
    @Size(max = 255)
    private String tipo;

    private List<ComponenteDTO> componentes;
}
