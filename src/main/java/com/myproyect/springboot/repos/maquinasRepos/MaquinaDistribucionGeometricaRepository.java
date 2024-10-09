package com.myproyect.springboot.repos.maquinasRepos;

import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionGeometrica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaquinaDistribucionGeometricaRepository extends JpaRepository<MaquinaDistribucionGeometrica, Integer> {
}

