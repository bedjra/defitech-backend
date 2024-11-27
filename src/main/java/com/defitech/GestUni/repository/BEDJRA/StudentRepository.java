package com.defitech.GestUni.repository.BEDJRA;

import com.defitech.GestUni.enums.NiveauEtude;
import com.defitech.GestUni.models.Bases.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Etudiant, Long> {
    List<Etudiant> findByNomAndPrenom(String nom, String prenom);

    List<Etudiant> findByParcoursNomParcoursAndNiveauEtude(String nomParcours, NiveauEtude niveau);


}

