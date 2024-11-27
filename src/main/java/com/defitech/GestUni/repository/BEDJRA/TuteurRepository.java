package com.defitech.GestUni.repository.BEDJRA;


import com.defitech.GestUni.models.Bases.Tuteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TuteurRepository extends JpaRepository<Tuteur, Long> {
   // Tuteur findByEtudiants_Id(Long etudiantId);
}
