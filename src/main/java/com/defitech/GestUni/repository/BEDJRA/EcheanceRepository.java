package com.defitech.GestUni.repository.BEDJRA;

import com.defitech.GestUni.enums.BEDJRA.StatutEcheance;
import com.defitech.GestUni.models.BEDJRA.Echeance;
import com.defitech.GestUni.models.BEDJRA.Paiement;
import com.defitech.GestUni.models.Bases.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EcheanceRepository extends JpaRepository<Echeance, Long> {

    List<Echeance> findByPaiement(Paiement paiement);


    List<Echeance> findByEtudiantAndStatut(Etudiant etudiant, StatutEcheance statut);
    List<Echeance> findByEtudiantAndStatutNot(Etudiant etudiant, StatutEcheance statut);

}
