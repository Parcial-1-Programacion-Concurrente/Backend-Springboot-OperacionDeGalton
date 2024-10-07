package com.myproyect.springboot.model;



import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VisualizacionServiceDTO {

    private List<ComponenteDTO> datosDistribucion;

    private String reporte;
}
