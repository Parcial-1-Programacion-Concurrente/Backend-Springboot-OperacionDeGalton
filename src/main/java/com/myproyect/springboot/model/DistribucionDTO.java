package com.myproyect.springboot.model;



import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class DistribucionDTO {

    private Long id;

    @NotNull
    private Integer numBolas;

    @NotNull
    private Integer numContenedores;

    private Map<String, Integer> datos;
}
