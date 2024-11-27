package com.defitech.GestUni.repository;

import com.defitech.GestUni.models.Bases.Professeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesseurRepository extends JpaRepository<Professeur, Long> {
}
