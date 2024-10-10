package com.myproyect.springboot.repos.maquinasRepos;

import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaquinaDistribucionCustomRepository extends JpaRepository<MaquinaDistribucionCustom, Integer> {
}

