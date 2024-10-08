package com.myproyect.springboot.model;




import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComponenteWorkerDTO {

    private Integer id;

    @NotNull
    private Integer componenteId;

    private GaltonBoardDTO galtonBoard;
}
