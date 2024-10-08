package com.myproyect.springboot.repos;

import com.myproyect.springboot.domain.synchronization.GaltonBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GaltonBoardRepository extends JpaRepository<GaltonBoard, Integer> {
}

