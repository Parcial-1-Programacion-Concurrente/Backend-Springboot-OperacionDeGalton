package com.myproyect.springboot.repos;

import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaquinaRepository extends JpaRepository<Maquina, Long> {
}

