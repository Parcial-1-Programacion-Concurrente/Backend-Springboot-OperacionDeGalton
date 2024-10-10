package com.myproyect.springboot.model;





import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaFactoryDTO {

    private Integer id;

    @NotNull
    private Integer maquinaId;

    @NotNull
    private String tipoMaquina;
}
