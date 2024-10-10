package com.myproyect.springboot.model;




import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class FabricaGaussDTO {

    private Integer id;

    @NotNull
    private String nombre;

    private OffsetDateTime dateCreated;
}
