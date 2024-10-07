package com.myproyect.springboot.model;



import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class EstacionTrabajoDTO {

    private Long id;

    @NotNull
    private String nombre;

    @NotNull
    private Long fabricaGaussId;

    @NotNull
    private String tipo;

    private Integer capacidadBuffer;

    private List<ComponenteDTO> bufferComponentes;
}

