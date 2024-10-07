package com.myproyect.springboot.repos;

import com.myproyect.springboot.domain.concurrency.EstacionTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstacionTrabajoRepository extends JpaRepository<EstacionTrabajo, Long> {
}

