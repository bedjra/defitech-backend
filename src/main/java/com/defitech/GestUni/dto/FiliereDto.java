package com.defitech.GestUni.dto;

import com.defitech.GestUni.enums.NiveauEtude;
import lombok.Data;

@Data
public class FiliereDto {
    private Long id;
    private String nomFiliere;
    private String description;
    private String parcoursNom;
    private NiveauEtude niveauEtude;

}
