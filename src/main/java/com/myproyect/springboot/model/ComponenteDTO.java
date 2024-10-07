package com.myproyect.springboot.model;



import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComponenteDTO {

    private Integer id;

    @NotNull
    @Size(max = 255)
    private String tipo;

    @NotNull
    private Double valorCalculado;
}
