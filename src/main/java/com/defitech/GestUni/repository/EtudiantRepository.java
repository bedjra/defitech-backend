package com.defitech.GestUni.repository;

import com.defitech.GestUni.enums.BEDJRA.StatutEcheance;
import com.defitech.GestUni.enums.BEDJRA.StatutScolarite;
import com.defitech.GestUni.enums.NiveauEtude;
import com.defitech.GestUni.models.Bases.Etudiant;
import com.defitech.GestUni.models.Bases.Filiere;
import com.defitech.GestUni.models.Bases.Parcours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    List<Etudiant> findByNomAndPrenom(String nom, String prenom);
    List<Etudiant> findByParcoursNomParcoursAndNiveauEtude(String nomParcours, NiveauEtude niveau);

    Optional<Etudiant> findFirstByNomAndPrenom(String nom, String prenom);


    @Query("SELECT COUNT(e) FROM Etudiant e")
    long countTotalEtudiants();

    @Query("SELECT e FROM Etudiant e WHERE e.filiere.nomFiliere = :nomFiliere AND e.niveauEtude = :niveauEtude")
    List<Etudiant> findAllByFiliereAndNiveauEtude(@Param("nomFiliere") String nomFiliere, @Param("niveauEtude") NiveauEtude niveauEtude);

    long countByParcours(Parcours parcours);

    long countByParcoursAndSexe(Parcours parcours, String sexe);

    List<Etudiant> findByFiliereAndNiveauEtude(Filiere filiere, NiveauEtude niveauEtude);

    List<Etudiant> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    Optional<Etudiant> findByMatricule(String matricule);

    Optional<Etudiant> findByEtudiantId(Long id);




    List<Etudiant> findByNomStartingWithOrPrenomStartingWith(String nom, String prenom);

    List<Etudiant> findByFiliereNomFiliereAndNiveauEtude(String nomFiliere, NiveauEtude niveauEtude);


    long countByFiliereAndNiveauEtudeAndParcours(Filiere filiere, NiveauEtude niveauEtude, Parcours parcours);


    long countByFiliereAndNiveauEtudeAndParcoursAndSexe(Filiere filiere, NiveauEtude niveauEtude, Parcours parcours, String sexe);






}
