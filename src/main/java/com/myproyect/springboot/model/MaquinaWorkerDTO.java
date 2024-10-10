package com.myproyect.springboot.model;


import com.myproyect.springboot.domain.concurrency.MaquinaWorker;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class MaquinaWorkerDTO extends MaquinaWorker {

    private Integer id;

    @NotNull
    private Integer maquinaId;

    private List<ComponenteWorkerDTO> componenteWorkers;
}
