package com.defitech.GestUni.dto.BEDJRA;

import java.util.List;

public class RappelEcheanceDto {
    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
    private List<EcheanceDto> echeances;
    private String tuteurMail;


    public Long getEtudiantId() {
        return etudiantId;
    }

    public void setEtudiantId(Long etudiantId) {
        this.etudiantId = etudiantId;
    }

    public String getEtudiantNom() {
        return etudiantNom;
    }

    public void setEtudiantNom(String etudiantNom) {
        this.etudiantNom = etudiantNom;
    }

    public String getEtudiantPrenom() {
        return etudiantPrenom;
    }

    public void setEtudiantPrenom(String etudiantPrenom) {
        this.etudiantPrenom = etudiantPrenom;
    }

    public List<EcheanceDto> getEcheances() {
        return echeances;
    }

    public void setEcheances(List<EcheanceDto> echeances) {
        this.echeances = echeances;
    }

    public String getTuteurMail() {
        return tuteurMail;
    }

    public void setTuteurMail(String tuteurMail) {
        this.tuteurMail = tuteurMail;
    }
}
