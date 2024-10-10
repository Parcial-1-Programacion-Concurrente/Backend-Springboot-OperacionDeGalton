package com.myproyect.springboot.repos;

import com.myproyect.springboot.domain.concurrency.ComponenteWorker;
import com.myproyect.springboot.domain.concurrency.MaquinaWorker;
import com.myproyect.springboot.domain.factory.maquinas.Maquina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponenteWorkerRepository extends JpaRepository<ComponenteWorker, Integer> {
    List<ComponenteWorker> findAllByMaquinaWorker(MaquinaWorker maquinaWorker);
}


