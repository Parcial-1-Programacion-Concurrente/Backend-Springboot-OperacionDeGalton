package com.myproyect.springboot.model;




import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComponenteDTO {

    private Integer id;

    @NotNull
    private String tipo;

    @NotNull
    private Double valorCalculado;

    private Integer estacionTrabajoId;
}
