package com.myproyect.springboot.repos;

import com.myproyect.springboot.domain.distribution.Distribucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistribucionRepository extends JpaRepository<Distribucion, Long> {
}

