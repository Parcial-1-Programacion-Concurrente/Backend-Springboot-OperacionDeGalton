package com.myproyect.springboot.model;




import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComponenteWorkerDTO {

    private Long id;

    @NotNull
    private Long componenteId;

    private GaltonBoardDTO galtonBoard;
}
