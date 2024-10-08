package com.myproyect.springboot.model;




import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GaltonBoardDTO {

    private Integer id;

    @NotNull
    private Integer numBolas;

    @NotNull
    private Integer numContenedores;

    private DistribucionDTO distribucion;

    @NotNull
    private String estado; // 'EN_SIMULACION', 'FINALIZADA'
}
