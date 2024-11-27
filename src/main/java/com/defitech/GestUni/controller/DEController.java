package com.defitech.GestUni.controller;

import com.defitech.GestUni.dto.UEDto;
import com.defitech.GestUni.enums.NiveauEtude;
import com.defitech.GestUni.models.Bases.Filiere;
import com.defitech.GestUni.models.Bases.Parcours;
import com.defitech.GestUni.models.Bases.Professeur;
import com.defitech.GestUni.models.Bases.UE;
import com.defitech.GestUni.service.FiliereServices;
import com.defitech.GestUni.service.ParcoursServices;
import com.defitech.GestUni.service.ProfesseurServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/de")
public class DEController {


    @Autowired
    private FiliereServices filiereService;
    @Autowired
    private ProfesseurServices professeurService;
    @Autowired
    private ParcoursServices parcoursService;


    //////////////////////////////////////////////---UE---///////////////////////////////////////////////////

    ///////////////////////////////////////////----Filiere----///////////////////////////////////////////

    @PostMapping("add-filiere")
    public Filiere createFiliere(@RequestBody Filiere filiere) {
        return filiereService.saveFiliere(filiere);
    }

    // Mettre à jour une filière
    @PutMapping("update-filiere/{id}")
    public Filiere updateFiliere(@PathVariable Long id, @RequestBody Filiere filiere) {
        filiere.setFiliereId(id);
        return filiereService.saveFiliere(filiere);
    }
//    @GetMapping("all-filieres-sans-etudiants")
//    public List<FiliereDto> getFilieresSansEtudiants() {
//        return filiereService.getFilieresDto();
//    }

    // Supprimer une filière
    @DeleteMapping("delfiliere/{id}")
    public void deleteFiliere(@PathVariable Long id) {
        filiereService.deleteFiliere(id);
    }

    @GetMapping("/filiereByparcours/{parcoursId}")
    public List<Filiere> getFilieresByParcours(@PathVariable Long parcoursId) {
        return filiereService.getFilieresByParcours(parcoursId);
    }


    ///////////////////////////////////// ----Professeur--------- //////////////////////////////////////

    @GetMapping("all-prof")
    public List<Professeur> getAllProfs() {
        return professeurService.getAllProfesseurs();
    }

    // Récupérer un professeur par son ID
    @GetMapping("profbyid/{id}")
    public Professeur getProfById(@PathVariable Long id) {
        return professeurService.getProfesseurById(id);
    }

    // Créer un nouveau professeur
    @PostMapping("add-prof")
    public Professeur createProf(@RequestBody Professeur professeur) {
        return professeurService.saveProfesseur(professeur);
    }



    // Supprimer un professeur
    @DeleteMapping("delete-prof/{id}")
    public void deleteProf(@PathVariable Long id) {
        professeurService.deleteProfesseur(id);
    }

    ////////////////////////////----{Parcours}----///////////////////////////////////////////////

    @GetMapping("all-parcours")
    public List<Parcours> getAllParcours() {
        return parcoursService.getAllParcours();
    }

    // Récupérer un parcours par son ID
//    @GetMapping("parcoursbyid/{id}")
//    public Parcours getParcoursById(@PathVariable Long id) {
//        return parcoursService.getParcoursById(id);
//    }

    // Créer un nouveau parcours
    @PostMapping("add-parcours")
    public Parcours createParcours(@RequestBody Parcours parcours) {
        return parcoursService.saveParcours(parcours);
    }

    // Mettre à jour un parcours
    @PutMapping("update-parcours/{id}")
    public Parcours updateParcours(@PathVariable Long id, @RequestBody Parcours parcours) {
        parcours.setParcoursId(id);
        return parcoursService.saveParcours(parcours);
    }

    // Supprimer un parcours
    @DeleteMapping("del-parcours/{id}")
    public void deleteParcours(@PathVariable Long id) {
        parcoursService.deleteParcours(id);
    }

///////////////////////affichage des niveaux en fonction des parcours /////////////////////
    @GetMapping("/niveauxByParcours/{parcoursId}")
    public List<NiveauEtude> getNiveauxByParcours(@PathVariable Long parcoursId) {
        return filiereService.getNiveauxByParcours(parcoursId);
    }


}
