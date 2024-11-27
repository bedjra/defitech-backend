package com.defitech.GestUni.service;

import com.defitech.GestUni.dto.BEDJRA.ParcoursStatsDTO;
import com.defitech.GestUni.models.Bases.Parcours;
import com.defitech.GestUni.repository.EtudiantRepository;
import com.defitech.GestUni.repository.FiliereRepository;
import com.defitech.GestUni.repository.ParcoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParcoursServices {
    @Autowired
    private ParcoursRepository parcoursRepository;
    @Autowired
    private EtudiantRepository etudiantRepository;
    @Autowired
    private FiliereRepository filiereRepository;

    public Parcours saveParcours(Parcours parcours) {
        return parcoursRepository.save(parcours);
    }

    public List<Parcours> getAllParcours() {
        return parcoursRepository.findAll();
    }

    public Optional<Parcours> getParcoursById(Long id) {
        return parcoursRepository.findById(id);
    }

    public void deleteParcours(Long id) {
        parcoursRepository.deleteById(id);
    }

    public Optional<Parcours> getParcoursByNom(String nomParcours) {
        return parcoursRepository.findByNomParcours(nomParcours);
    }

    public List<ParcoursStatsDTO> getAllParcoursWithStats() {
        List<Parcours> parcoursList = parcoursRepository.findAll();
        List<ParcoursStatsDTO> statsList = new ArrayList<>();

        for (Parcours parcours : parcoursList) {
            ParcoursStatsDTO statsDTO = new ParcoursStatsDTO();
            statsDTO.setId(parcours.getParcoursId());
            statsDTO.setNom(parcours.getNomParcours());

            long totalEtudiants = etudiantRepository.countByParcours(parcours);
            statsDTO.setTotalEtudiants(totalEtudiants);

            long totalGarcons = etudiantRepository.countByParcoursAndSexe(parcours, "M");
            long totalFilles = etudiantRepository.countByParcoursAndSexe(parcours, "F");

            statsDTO.setTotalGarcons(totalGarcons);
            statsDTO.setTotalFilles(totalFilles);

            statsList.add(statsDTO);
        }

        return statsList;
    }


}
