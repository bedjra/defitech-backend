package com.defitech.GestUni.controller;

import com.defitech.GestUni.enums.BEDJRA.*;
import com.defitech.GestUni.models.BEDJRA.Paiement;
import com.defitech.GestUni.models.Bases.Filiere;
import com.defitech.GestUni.repository.BEDJRA.PaiementRepository;
import com.defitech.GestUni.repository.EtudiantRepository;

import jakarta.validation.Valid;
import org.springframework.ui.Model;
import com.defitech.GestUni.dto.BEDJRA.*;
import com.defitech.GestUni.enums.NiveauEtude;
import com.defitech.GestUni.models.Bases.Etudiant;
import com.defitech.GestUni.repository.BEDJRA.EcheanceRepository;
import com.defitech.GestUni.service.BEDJRA.PaiementService;
import com.defitech.GestUni.service.BEDJRA.StudentServices;
import com.defitech.GestUni.service.FiliereServices;
import com.defitech.GestUni.service.ParcoursServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.Document;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/comptable")
public class ComptableController {

    @Autowired
    private StudentServices studentServices;
    @Autowired
    private PaiementRepository paiementRepository;
    @Autowired
    private EtudiantRepository etudiantRepository;
    @Autowired
    private EcheanceRepository echeanceRepository;
    @Autowired
    private ParcoursServices parcoursServices;
    @Autowired
    private PaiementService paiementService;
    @Autowired
    private FiliereServices filiereServices;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////// TABLEAU DE BORD //////////////////////////////////////////
    ///// NMBRE TOTAL
    @GetMapping("/count")
    public long getTotalEtudiants() {
        return paiementService.getTotalEtudiants();
    }

    //    // NBRE : PARTIE et EN ATTENTE
    @GetMapping("/parti-attente")
    public long getEtudiantsNonSoldes() {
        return paiementService.compterEtudiantsNonSoldes();
    }

    //    // NBRE : PAYEE
    @GetMapping("/Tpayee")
    public long obtenirNombrePaiementsSoldes() {
        return paiementService.compterEtudiantsSoldes();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////// AJOUTER ETUDIANTS //////////////////////////////////////////
    // Endpoint pour ajouter un étudiant
    @PostMapping("/ajout_etudiant")
    public ResponseEntity<?> ajouterEtudiant(@Valid @RequestBody StudentDto studentDto, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            result.getFieldErrors().forEach(error -> {
                errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
            });
            return ResponseEntity.badRequest().body(errors.toString());
        }
        // Vérifier si l'étudiant existe déjà
        if (studentServices.studentExists(studentDto)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Étudiant déjà ajouté.");
        }

        // Si l'étudiant n'existe pas, procéder à l'ajout
        Etudiant etudiant = studentServices.saveEtudiant(studentDto);
        return ResponseEntity.ok(Collections.singletonMap("message", "Étudiant ajouté avec succès"));
    }


    @PutMapping("/update_etudiant/{matricule}")
    public ResponseEntity<StudentDto> updateEtudiant(@PathVariable String matricule, @RequestBody StudentDto studentDto) {
        try {
            StudentDto updatedStudent = studentServices.updateEtudiant(matricule, studentDto);
            return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


    ///////// DELETE ETUDIANT ////////////////////////////////////////////////////
    @DeleteMapping("/etudiant/{matricule}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable String matricule) {
        studentServices.deleteEtudiant(matricule);
        return ResponseEntity.noContent().build();
    }

    /////////////GET ALL ETUDIANTS //////////////////////////////////////////////
    @GetMapping("/etudiant")
    public ResponseEntity<List<Etudiant>> getAllEtudiants() {
        List<Etudiant> etudiants = studentServices.getAllEtudiants();
        return new ResponseEntity<>(etudiants, HttpStatus.OK);
    }

    /////////////GET ETUDIANT BY ID //////////////////////////////////////////////

    @GetMapping("/etudiant/{matricule}")
    public ResponseEntity<StudentDto> getEtudiantByMatricule(@PathVariable String matricule) {
        StudentDto dto = studentServices.getStudentDtoByMatricule(matricule);
        return ResponseEntity.ok(dto);
    }


    /////////////GET ETUDIANT BY PARCOURS AND NIVEAU //////////////////////////////////////////////
    @GetMapping("/{nomParcours}/{niveau}")
    public ResponseEntity<List<StudentDto>> getEtudiantsByParcoursAndNiveau(@PathVariable String nomParcours, @PathVariable NiveauEtude niveau) {

        List<StudentDto> etudiants = studentServices.getEtudiantsByParcoursAndNiveau(nomParcours, niveau);

        if (etudiants.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(etudiants);
    }

    /////////////GET ETUDIANT BY FILIERE AND NIVEAU //////////////////////////////////////////////

    /////////////GET ETUDIANT BY NOM AND PRENOM //////////////////////////////////////////////
    @GetMapping("/search")
    public List<StudentDto> searchEtudiants(@RequestParam String nom, @RequestParam String prenom) {
        return studentServices.searchEtudiantsByNomAndPrenom(nom, prenom);
    }

    @GetMapping("/filieres/parcours/{parcoursId}")
    public ResponseEntity<List<DtoFiliere>> getFilieresByParcours(@PathVariable Long parcoursId) {
        List<DtoFiliere> filieres = filiereServices.getFilieresByParcoursId(parcoursId);
        return ResponseEntity.ok(filieres);
    }

    // Endpoint pour récupérer les étudiants par filière et niveau
    @GetMapping("/filiere/{nomFiliere}/niveau/{niveauEtude}")
    public ResponseEntity<List<StudentDto>> getEtudiantsByFiliereAndNiveau(@PathVariable String nomFiliere, @PathVariable NiveauEtude niveauEtude) {

        List<StudentDto> etudiants = studentServices.getEtudiantsByFiliereAndNiveau(nomFiliere, niveauEtude);
        return ResponseEntity.ok(etudiants);
    }


    /////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////    PAIEMENTS    ///////////////////////////////////////////////////
    @PostMapping("/ajout_paiement")
    public PaiementDto effectuerPaiement(@RequestBody PaiementDto paiementDto) {
        return paiementService.effectuerPaiement(paiementDto);
    }

    @GetMapping("/paiement/{matricule}")
    public ResponseEntity<PaiementDto> getPaiementByEtudiantMatricule(@PathVariable String matricule) {
        PaiementDto paiementDto = paiementService.getPaiementByEtudiantMatricule(matricule);
        return ResponseEntity.ok(paiementDto);
    }

    @GetMapping("/etudiants/en-cours")
    public ResponseEntity<List<PaiementDto>> getPaiementsEnCours() {
        List<PaiementDto> paiements = paiementService.getPaiementsEnCours();
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/fiche-impression")
    public String getFicheImpression(Model model) {
        List<PaiementDto> paiements = paiementService.getPaiementsEnCours();
        model.addAttribute("paiements", paiements);
        LocalDate currentDate = LocalDate.now();
        model.addAttribute("currentDate", currentDate);

        return "fiche"; // Nom du template HTML
    }


    @GetMapping("/etudiant/search/nom&prenom")
    public List<Etudiant> searchEtudiants(@RequestParam("search") String searchTerm) {
        return paiementService.searchByNomOrPrenom(searchTerm);
    }

//    @PutMapping("/update_paiement/")
//    public ResponseEntity<PaiementDto> updatePaiement(@RequestBody PaiementDto paiementDto) {
//            return paiementService.updatePaiement(paiementDto);
//    }


    @GetMapping("/by-filiere-and-niveau")
    public ResponseEntity<List<PaiementDto>> getDernierPaiementByFiliereAndNiveau(

            @RequestParam String nomFiliere, @RequestParam String niveauEtude) {
        List<PaiementDto> paiements = paiementService.getDernierPaiementByFiliereAndNiveau(nomFiliere, niveauEtude);
        return ResponseEntity.ok(paiements);
    }


    @GetMapping("/paiements")
    public List<PaiementDto> getAllPaiements() {
        return paiementService.getAllPaiements();
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //////////////////////      RAPPELS     ////////////////////////////////////////////
    // Endpoint pour récupérer la liste des paiements en attente
    @GetMapping("/total/rappel")
    public ResponseEntity<List<PaiementDto>> getPaiementsPourTousLesEtudiants() {
        try {
            // Appel au service pour récupérer les paiements de tous les étudiants
            List<PaiementDto> paiements = paiementService.getPaiementsPourTousLesEtudiants();

            // Si la liste est vide, retourner une réponse "No Content"
            if (paiements.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            // Retourner la liste avec le code de succès 200
            return new ResponseEntity<>(paiements, HttpStatus.OK);

        } catch (Exception e) {
            // En cas d'erreur, retourner une réponse avec un code 500 (Internal Server Error)
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rappel")
    public ResponseEntity<List<PaiementDto>> getPaiementsAvecEcheanceNonReglee() {
        try {
            List<PaiementDto> paiementsNonRegles = paiementService.getPaiementsAvecEcheanceNonReglee();

            if (paiementsNonRegles.isEmpty()) {
                // Retourner une réponse avec un statut HTTP 204 (No Content) si aucune échéance n'est trouvée
                return ResponseEntity.noContent().build();
            }

            // Retourner la liste avec un statut HTTP 200 (OK)
            return ResponseEntity.ok(paiementsNonRegles);

        } catch (Exception e) {
            // Gestion des erreurs (peut être améliorée selon les besoins)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }




    //    // Endpoint pour récupérer le nombre total de paiements en attente
   @GetMapping("/rappel/count")
    public ResponseEntity<Long> getNombrePaiementsNonRegles() {
        long nombreNonRegles = paiementService.compterPaiementsAvecEcheanceNonReglee();
        return ResponseEntity.ok(nombreNonRegles);
    }

//    public ResponseEntity<Long> getNombrePaiementsEnAttente() {
//        long nombreEnAttente = paiementService.getNombrePaiementsEnAttente();
//        return ResponseEntity.ok(nombreEnAttente);
//    }


    /////////////////////////////////////////////////////////////////////////////////////
    //////////////////////     ECHEANCES    ////////////////////////////////////////////

    ///////////// Etudiants : PARTIE et EN ATTENTE
    //liste des renvois
    @GetMapping("/statut")
    public List<Etudiant> getEtudiantsParStatuts() {
        List<StatutEcheance> statuts = Arrays.asList(StatutEcheance.PARTIELLEMENT_PAYEE, StatutEcheance.EN_ATTENTE);
        return studentServices.getEtudiantsParStatuts(statuts);
    }


    /////////////////////////////////////////////////////////////////////////////////////
    //////////////////////     STATISTIQUE PARCOURS - SEXE   ////////////////////////////////////////////
    @GetMapping("/stats")
    public List<ParcoursStatsDTO> getParcoursWithStats() {
        return parcoursServices.getAllParcoursWithStats();
    }


    @GetMapping("/statistiques-etudiants/{parcoursId}") // Endpoint pour obtenir les statistiques par parcours
    public List<ParcoursStatsDTO> getStatistiquesByParcours(@PathVariable Long parcoursId) {
        return studentServices.getStatistiquesEtudiantsParParcours(parcoursId);
    }


    /////////////////////////////////////////////////////////////////////////////////////
    //////////////////////     ENUMS    ////////////////////////////////////////////
    @GetMapping("/niveau")
    public ResponseEntity<List<String>> getNiveaux() {
        List<String> niveaux = Arrays.stream(NiveauEtude.values()).map(Enum::name).collect(Collectors.toList());
        return ResponseEntity.ok(niveaux);
    }


    @GetMapping("/boursier")
    public ResponseEntity<List<String>> getBoursier() {
        List<String> boursier = Arrays.stream(Statutboursier.values()).map(Enum::name).collect(Collectors.toList());
        return ResponseEntity.ok(boursier);
    }

    @GetMapping("/modalites")
    public ResponseEntity<List<String>> getModalites() {
        List<String> modalites = Arrays.stream(TypeModalite.values()).map(Enum::name).collect(Collectors.toList());
        return ResponseEntity.ok(modalites);
    }

    @GetMapping("/mentions")
    public ResponseEntity<List<String>> getMentions() {
        List<String> mentions = Arrays.stream(MentionBac.values()).map(Enum::name).collect(Collectors.toList());
        return ResponseEntity.ok(mentions);
    }

    @GetMapping("/filiere")
    public List<DtoFiliere> getAllFilieres() {
        return paiementService.getAllFiliereDtos();
    }

    @GetMapping("/{parcoursId}")
    public ResponseEntity<List<Filiere>> getFiliereByParcours(@PathVariable Long parcoursId) {
        List<Filiere> filieres = filiereServices.findByParcoursId(parcoursId);
        return new ResponseEntity<>(filieres, HttpStatus.OK);
    }

    @GetMapping("/niveau/{parcoursId}")
    public List<NiveauEtude> getNiveauxByParcours(@PathVariable("parcoursId") Long parcoursId) {
        return filiereServices.getNiveauxByParcours(parcoursId);
    }


    /////////////////////////////////////////////////////////////////////////////////////
    //////////////////////     IMPRESSION    ////////////////////////////////////////////
    @GetMapping("/imprimerEtudiants/{nomFiliere}/niveau/{niveauEtude}")
    public ResponseEntity<String> imprimerEtudiants(@PathVariable String nomFiliere, @PathVariable NiveauEtude niveauEtude) {
        List<StudentDto> etudiants = studentServices.getEtudiantsByFiliereAndNiveau(nomFiliere, niveauEtude);

        // Afficher les informations de chaque étudiant
        for (StudentDto etudiant : etudiants) {
            etudiant.InformationsEtudiant();
        }

        return ResponseEntity.ok("Liste des étudiants imprimée dans la console.");
    }


//    @GetMapping("/etudiants/en-cours/imprimer")
//    public ResponseEntity<List<PaiementDto>> getListeRenvoiTous() {
//        List<PaiementDto> paiements = paiementService.getPaiementsPourTousLesEtudiants();
//        if (paiements.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }
//        // Afficher les informations de chaque paiement
//        for (PaiementDto paiement : paiements) {
//
//            System.out.println("Matricule de l'etudiant : " + paiement.getEtudiantMatricule());
//            System.out.println("Nom de l'étudiant : " + paiement.getEtudiantNom());
//            System.out.println("Prénom de l'étudiant : " + paiement.getEtudiantPrenom());
//            System.out.println("Montant déjà payé : " + paiement.getMontantDejaPaye());
//            System.out.println("Reste à payer : " + paiement.getResteEcolage());
//            // Vous pouvez ajouter d'autres informations selon le DTO
//            System.out.println("------------------------------------------");
//        }
//        return ResponseEntity.ok(paiements);
//    }

//    public ResponseEntity<String> imprimerPaiementsEnCours() {
//        List<PaiementDto> paiements = paiementService.getPaiementsEnCours();
//
//        // Afficher les informations de chaque paiement
//        for (PaiementDto paiement : paiements) {
//
//            System.out.println("Matricule de l'etudiant : " + paiement.getEtudiantMatricule());
//            System.out.println("Nom de l'étudiant : " + paiement.getEtudiantNom());
//            System.out.println("Prénom de l'étudiant : " + paiement.getEtudiantPrenom());
//            System.out.println("Montant déjà payé : " + paiement.getMontantDejaPaye());
//            System.out.println("Reste à payer : " + paiement.getResteEcolage());
//            // Vous pouvez ajouter d'autres informations selon le DTO
//            System.out.println("------------------------------------------");
//        }
//
//        return ResponseEntity.ok("Liste des paiements en cours imprimée dans la console.");
//    }

//    @GetMapping("/tous")
//    public ResponseEntity<List<PaiementDto>> getListeRenvoiTous() {
//        List<PaiementDto> paiements = paiementService.getPaiementsPourTousLesEtudiants();
//        if (paiements.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.ok(paiements);
//    }

    @GetMapping("/tous")
    public List<PaiementDto> getListeRenvoiTous() {
        return paiementService.getListeRenvoiTous();
    }


    @GetMapping("/dernier/{etudiantId}")
    public ResponseEntity<Paiement> getLastPaiement(@PathVariable Long etudiantId) {
        // Utiliser un Optional pour gérer la recherche de l'étudiant
        Optional<Etudiant> optionalEtudiant = studentServices.getEtudiantById(etudiantId);

        if (optionalEtudiant.isPresent()) {
            Etudiant etudiant = optionalEtudiant.get();
            try {
                Paiement dernierPaiement = paiementService.getLastPaiementForEtudiant(etudiant);
                return new ResponseEntity<>(dernierPaiement, HttpStatus.OK);
            } catch (RuntimeException e) {
                // En cas de problème lors de la recherche du paiement
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            // Gérer le cas où l'étudiant n'est pas trouvé
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}


