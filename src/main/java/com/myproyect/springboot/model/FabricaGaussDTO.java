package com.myproyect.springboot.model;




import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class FabricaGaussDTO {

    private Long id;

    @NotNull
    private String nombre;

    private List<EstacionTrabajoDTO> estaciones;

    private LineaEnsamblajeDTO lineaEnsamblaje;

    private OffsetDateTime dateCreated;
}
