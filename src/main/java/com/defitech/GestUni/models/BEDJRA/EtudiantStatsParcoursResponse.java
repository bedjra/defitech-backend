package com.defitech.GestUni.models.BEDJRA;

import com.defitech.GestUni.enums.NiveauEtude;

import java.util.Map;

public class EtudiantStatsParcoursResponse {
    private Map<String, Map<NiveauEtude, Long>> etudiantsParFiliereNiveau;
    private Map<String, Map<NiveauEtude, Map<String, Long>>> garconsFillesParFiliereNiveau;

    // Getters and Setters

    public Map<String, Map<NiveauEtude, Long>> getEtudiantsParFiliereNiveau() {
        return etudiantsParFiliereNiveau;
    }

    public void setEtudiantsParFiliereNiveau(Map<String, Map<NiveauEtude, Long>> etudiantsParFiliereNiveau) {
        this.etudiantsParFiliereNiveau = etudiantsParFiliereNiveau;
    }

    public Map<String, Map<NiveauEtude, Map<String, Long>>> getGarconsFillesParFiliereNiveau() {
        return garconsFillesParFiliereNiveau;
    }

    public void setGarconsFillesParFiliereNiveau(Map<String, Map<NiveauEtude, Map<String, Long>>> garconsFillesParFiliereNiveau) {
        this.garconsFillesParFiliereNiveau = garconsFillesParFiliereNiveau;
    }

}
