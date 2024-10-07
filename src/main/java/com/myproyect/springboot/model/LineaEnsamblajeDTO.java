package com.myproyect.springboot.model;





import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class LineaEnsamblajeDTO {

    private Long id;

    @NotNull
    private Long fabricaGaussId;

    private Integer capacidadBuffer;

    private List<ComponenteDTO> bufferCompartido;
}
