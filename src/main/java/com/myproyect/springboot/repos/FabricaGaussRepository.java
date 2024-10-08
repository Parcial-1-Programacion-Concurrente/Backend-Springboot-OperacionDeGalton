package com.myproyect.springboot.repos;

import com.myproyect.springboot.domain.factory.FabricaGauss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FabricaGaussRepository extends JpaRepository<FabricaGauss, Integer> {
}

