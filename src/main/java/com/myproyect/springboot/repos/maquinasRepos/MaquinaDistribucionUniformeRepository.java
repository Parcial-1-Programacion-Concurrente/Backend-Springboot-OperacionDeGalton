package com.myproyect.springboot.repos.maquinasRepos;

import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionUniforme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaquinaDistribucionUniformeRepository extends JpaRepository<MaquinaDistribucionUniforme, Integer> {
}

