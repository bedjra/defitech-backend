package com.defitech.GestUni.dto.BEDJRA;

import com.defitech.GestUni.enums.NiveauEtude;

public class ParcoursStatsDTO {
    private Long id;
    private String nom;
    private long totalEtudiants;
    private long totalGarcons;
    private long totalFilles;
    private NiveauEtude niveau;
    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public long getTotalEtudiants() {
        return totalEtudiants;
    }

    public void setTotalEtudiants(long totalEtudiants) {
        this.totalEtudiants = totalEtudiants;
    }

    public long getTotalGarcons() {
        return totalGarcons;
    }

    public void setTotalGarcons(long totalGarcons) {
        this.totalGarcons = totalGarcons;
    }

    public long getTotalFilles() {
        return totalFilles;
    }

    public void setTotalFilles(long totalFilles) {
        this.totalFilles = totalFilles;
    }

    public NiveauEtude getNiveau() {
        return niveau;
    }

    public void setNiveau(NiveauEtude niveau) {
        this.niveau = niveau;
    }
}