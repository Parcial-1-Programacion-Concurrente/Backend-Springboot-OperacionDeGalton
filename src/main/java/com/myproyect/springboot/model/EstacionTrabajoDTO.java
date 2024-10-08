package com.myproyect.springboot.model;



import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class EstacionTrabajoDTO {

    private Integer id;

    @NotNull
    private String nombre;

    @NotNull
    private Integer fabricaGaussId;

    @NotNull
    private String tipo;

    private Integer capacidadBuffer;

    private List<ComponenteDTO> bufferComponentes;
}

