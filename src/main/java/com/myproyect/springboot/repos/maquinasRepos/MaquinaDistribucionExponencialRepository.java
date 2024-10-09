package com.myproyect.springboot.repos.maquinasRepos;

import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionExponencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaquinaDistribucionExponencialRepository extends JpaRepository<MaquinaDistribucionExponencial, Integer> {
}

