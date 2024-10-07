package com.myproyect.springboot.model;




import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComponenteDTO {

    private Long id;

    @NotNull
    private String tipo;

    @NotNull
    private Double valorCalculado;

    private Long estacionTrabajoId;
}
