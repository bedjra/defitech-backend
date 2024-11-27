package com.defitech.GestUni.repository;

import com.defitech.GestUni.models.Bases.UE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UERepository extends JpaRepository<UE, Long> {

    List<UE> findByFilieres_filiereId(Long filiereId);
}
