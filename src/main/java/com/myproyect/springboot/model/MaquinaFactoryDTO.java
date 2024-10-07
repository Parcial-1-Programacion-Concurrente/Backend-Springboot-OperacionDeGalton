package com.myproyect.springboot.model;





import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaFactoryDTO {

    private Long id;

    @NotNull
    private Long maquinaId;

    @NotNull
    private String tipoMaquina;
}
