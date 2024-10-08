package com.myproyect.springboot.repos;

import com.myproyect.springboot.domain.concurrency.LineaEnsamblaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineaEnsamblajeRepository extends JpaRepository<LineaEnsamblaje, Integer> {
}

