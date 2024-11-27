package com.defitech.GestUni.dto;

import com.defitech.GestUni.enums.NiveauEtude;
import lombok.Data;

@Data
public class EtudiantDto {
    private Long etudiantId;
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private NiveauEtude niveauEtude;
    private String parcoursNom;
    private String filiereNom;
    private String tuteurNom;
}
