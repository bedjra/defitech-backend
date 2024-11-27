package com.defitech.GestUni.service;

import com.defitech.GestUni.dto.BEDJRA.DtoFiliere;
import com.defitech.GestUni.dto.BEDJRA.FiliereStatsDTO;
import com.defitech.GestUni.dto.BEDJRA.ParcoursStatsDTO;
import com.defitech.GestUni.dto.UEDto;
import com.defitech.GestUni.enums.NiveauEtude;
import com.defitech.GestUni.models.Bases.Filiere;
import com.defitech.GestUni.models.Bases.Parcours;
import com.defitech.GestUni.repository.EtudiantRepository;
import com.defitech.GestUni.repository.FiliereRepository;
import com.defitech.GestUni.repository.ParcoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FiliereServices {
    @Autowired
    private FiliereRepository filiereRepository;
    @Autowired
    private ParcoursRepository parcoursRepository;
    @Autowired
    private EtudiantRepository etudiantRepository;

    public Filiere saveFiliere(Filiere filiere) {
        if (filiere.getParcours() != null) {
            Optional<Parcours> parcoursOpt = parcoursRepository.findById(filiere.getParcours().getParcoursId());
            if (parcoursOpt.isEmpty()) {
                throw new IllegalArgumentException("Parcours avec ID " + filiere.getParcours().getParcoursId() + " n'existe pas.");
            }
            filiere.setParcours(parcoursOpt.get());
        }
        return filiereRepository.save(filiere);
    }

    public List<Filiere> getFilieresByParcours(Long parcoursId) {
        return filiereRepository.findByParcours_ParcoursId(parcoursId);
    }

    public List<Filiere> getAllFilieres() {
        return filiereRepository.findAll();
    }

    public Optional<Filiere> getFiliereById(Long id) {
        return filiereRepository.findById(id);
    }

    public void deleteFiliere(Long id) {
        filiereRepository.deleteById(id);
    }



    private static final Map<Long, List<NiveauEtude>> niveauxParParcours = new HashMap<>();

    static {
        niveauxParParcours.put(1L, List.of(NiveauEtude.PREMIERE_ANNEE, NiveauEtude.DEUXIEME_ANNEE, NiveauEtude.TROISIEME_ANNEE)); // Licence du jour
        niveauxParParcours.put(3L, List.of(NiveauEtude.TROISIEME_ANNEE)); // Licence du soir
        niveauxParParcours.put(2L, List.of(NiveauEtude.PREMIERE_ANNEE, NiveauEtude.DEUXIEME_ANNEE)); // BTS
    }

    public List<NiveauEtude> getNiveauxByParcours(Long parcoursId) {
        return niveauxParParcours.getOrDefault(parcoursId, List.of());
    }

    public List<DtoFiliere> getFilieresByParcoursId(Long parcoursId) {
        List<Filiere> filieres = filiereRepository.findByParcoursParcoursId(parcoursId);

        return filieres.stream()
                .map(filiere -> {
                    DtoFiliere dto = new DtoFiliere();
                    dto.setNomFiliere(filiere.getNomFiliere());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public List<ParcoursStatsDTO> getStatistiquesEtudiantsParParcours(String nomParcours) {
        // Chercher le parcours par son nom
        Optional<Parcours> optionalParcours = parcoursRepository.findByNomParcours(nomParcours);

        if (optionalParcours.isPresent()) {
            Parcours parcours = optionalParcours.get();
            // Récupérer uniquement les filières associées à ce parcours
            List<Filiere> filieres = filiereRepository.findByParcours(parcours);
            List<ParcoursStatsDTO> statsList = new ArrayList<>();

            // Itérer sur chaque filière
            for (Filiere filiere : filieres) {
                // Itérer sur chaque niveau d'étude
                for (NiveauEtude niveauEtude : NiveauEtude.values()) {
                    // Compter le nombre d'étudiants pour chaque combinaison (filière, niveau, parcours)
                    long totalEtudiants = etudiantRepository.countByFiliereAndNiveauEtudeAndParcours(filiere, niveauEtude, parcours);

                    // Compter le nombre de garçons et de filles
                    long totalGarcons = etudiantRepository.countByFiliereAndNiveauEtudeAndParcoursAndSexe(filiere, niveauEtude, parcours, "M");
                    long totalFilles = etudiantRepository.countByFiliereAndNiveauEtudeAndParcoursAndSexe(filiere, niveauEtude, parcours, "F");

                    // Créer le DTO pour chaque combinaison
                    ParcoursStatsDTO statsDTO = new ParcoursStatsDTO();
                    statsDTO.setNom(filiere.getNomFiliere());
                    statsDTO.setNiveau(NiveauEtude.valueOf(niveauEtude.toString()));
                    statsDTO.setTotalGarcons(totalGarcons);
                    statsDTO.setTotalFilles(totalFilles);
                    statsDTO.setTotalEtudiants(totalEtudiants);

                    // Ajouter le DTO à la liste de statistiques
                    statsList.add(statsDTO);
                }
            }

            return statsList;
        } else {
            throw new RuntimeException("Le parcours avec le nom " + nomParcours + " n'existe pas.");
        }
    }

    public List<Filiere> findByParcoursId(Long parcoursId) {
        return filiereRepository.findByParcoursParcoursId(parcoursId);
    }



}
