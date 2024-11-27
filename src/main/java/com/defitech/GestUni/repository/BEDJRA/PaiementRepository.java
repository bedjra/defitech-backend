package com.defitech.GestUni.repository.BEDJRA;

import com.defitech.GestUni.enums.BEDJRA.StatutEcheance;
import com.defitech.GestUni.enums.BEDJRA.StatutScolarite;
import com.defitech.GestUni.models.BEDJRA.Paiement;
import com.defitech.GestUni.models.Bases.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {




    List<Paiement> findByEtudiant(Etudiant etudiant);

    @Query("SELECT DISTINCT p.etudiant FROM Paiement p JOIN p.echeances e WHERE e.statut IN (:statuts)")
    List<Etudiant> findEtudiantsByStatutEcheance(@Param("statuts") List<StatutEcheance> statuts);



    List<Paiement> findByEtudiantIn(List<Etudiant> etudiants);



    Paiement findTopByEtudiantOrderByDatePaiementDesc(Etudiant etudiant);


    @Query("SELECT SUM(p.montantActuel) FROM Paiement p WHERE p.etudiant = :etudiant")
    long sumMontantByEtudiant(@Param("etudiant") Etudiant etudiant);

    List<Paiement> findByStatutScolarite(StatutScolarite statutScolarite);



    // Compter le nombre d'étudiants uniques dont le reste à payer est 0
    @Query("SELECT COUNT(DISTINCT p.etudiant) FROM Paiement p WHERE p.resteEcolage = 0")
    long countEtudiantsSoldes();

    // Compter le nombre d'étudiants uniques dont le reste à payer est supérieur 0
    @Query("SELECT COUNT(DISTINCT p.etudiant.id) FROM Paiement p WHERE p.statutScolarite = 'EN_COURS'")
    long countStudentsWithEnCoursStatus();

    boolean existsByEtudiantAndStatutScolarite(Etudiant etudiant, StatutScolarite statut);


    @Query("SELECT p FROM Paiement p WHERE p.datePaiement = (" +
            "SELECT MAX(p2.datePaiement) FROM Paiement p2 WHERE p2.etudiant.id = p.etudiant.id) " +
            "AND p.statutScolarite = 'EN_COURS' AND p.datePaiement <= :currentDate")
    List<Paiement> findPaiementsEncoreEnCours(@Param("currentDate") LocalDate currentDate);


    @Query("SELECT p FROM Paiement p WHERE p.etudiant = :etudiantP ORDER BY p.datePaiement DESC")
    List<Paiement> findLastPaiement(@Param("etudiantP") Etudiant etudiantP);

}

