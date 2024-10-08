package com.myproyect.springboot.model.maquinas;



import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import com.myproyect.springboot.model.ComponenteDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public abstract class MaquinaDTO {

    @NotNull
    @NotEmpty
    private Integer id;

    @NotNull
    private int numeroComponentesRequeridos;

    @NotNull
    private String tipo;

    @NotNull
    private String estado;

    @NotNull
    private List<ComponenteDTO> componentes;

    private Integer galtonBoardId;

}
