package com.defitech.GestUni.repository;

import com.defitech.GestUni.enums.NiveauEtude;
import com.defitech.GestUni.models.BEDJRA.Paiement;
import com.defitech.GestUni.models.Bases.Etudiant;
import com.defitech.GestUni.models.Bases.Filiere;
import com.defitech.GestUni.models.Bases.Parcours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FiliereRepository extends JpaRepository<Filiere, Long> {
    List<Filiere> findByParcours_ParcoursId(Long parcoursId);

    Optional<Filiere> findByNomFiliere(String nomFiliere);

    List<Filiere> findByParcours(Parcours parcours); // Méthode pour récupérer les filières par parcours

    List<Filiere> findAll();



    List<Filiere> findByParcoursParcoursId(Long parcoursId); // Utiliser le nom du champ
}
