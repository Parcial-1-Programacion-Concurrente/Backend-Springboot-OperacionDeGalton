package com.myproyect.springboot.domain.factory.maquinas;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maquinas_distribucion_binomial")
@Getter
@Setter
public class MaquinaDistribucionBinomial extends Maquina {
}

