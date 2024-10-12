package com.myproyect.springboot.repos.maquinasRepos;

import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionPoisson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaquinaRepository extends JpaRepository<Maquina, Integer> {
    Optional<Maquina> findByGaltonBoardId(Integer galtonBoardId);
}

