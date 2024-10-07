package com.myproyect.springboot.repos;

import com.myproyect.springboot.domain.concurrency.Componente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponenteRepository extends JpaRepository<Componente, Long> {
}

