package com.myproyect.springboot.repos;

import com.myproyect.springboot.domain.concurrency.MaquinaWorker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaquinaWorkerRepository extends JpaRepository<MaquinaWorker, Integer> {
}

