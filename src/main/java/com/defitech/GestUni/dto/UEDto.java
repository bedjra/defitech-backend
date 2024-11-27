package com.defitech.GestUni.dto;

import com.defitech.GestUni.enums.NiveauEtude;
import com.defitech.GestUni.enums.TypeSemestre;
import lombok.Data;

import java.util.List;

@Data
public class UEDto {
    private Long ueId;
    private String libelle;
    private String code;
    private int credit;
    private String descriptUe;
    private String typeUe;
    private NiveauEtude niveauEtude;
    private TypeSemestre typeSemestre;
    private String professeurNom;
    private List<String> filiereNom;
}
