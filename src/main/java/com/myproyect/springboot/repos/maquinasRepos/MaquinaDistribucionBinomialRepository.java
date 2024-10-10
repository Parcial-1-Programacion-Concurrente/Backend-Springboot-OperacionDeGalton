package com.myproyect.springboot.repos.maquinasRepos;

import com.myproyect.springboot.domain.factory.maquinas.MaquinaDistribucionBinomial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaquinaDistribucionBinomialRepository extends JpaRepository<MaquinaDistribucionBinomial, Integer> {
}

