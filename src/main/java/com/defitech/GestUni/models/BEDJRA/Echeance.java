package com.defitech.GestUni.models.BEDJRA;

import com.defitech.GestUni.enums.BEDJRA.StatutEcheance;
import com.defitech.GestUni.enums.BEDJRA.TypeModalite;
import com.defitech.GestUni.models.Bases.Etudiant;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "echeance")
public class Echeance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long echeanceId;

    private long resteSurEcheance;
    @Enumerated(EnumType.STRING)
    private TypeModalite typeModalite;
    @ManyToOne
    @JoinColumn(name = "paiement_id")
    private Paiement paiement;
    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;
    private LocalDate dateEnvoi;

    private long nombreEcheances;
    private  long montantParEcheance;
    private LocalDate dateEcheance;

    @Enumerated(EnumType.STRING)
    private StatutEcheance statut;


    /////////////getters et setters//////////////////////////////////////////////////////////////////


    public Long getEcheanceId() {
        return echeanceId;
    }

    public void setEcheanceId(Long echeanceId) {
        this.echeanceId = echeanceId;
    }

    public long getResteSurEcheance() {
        return resteSurEcheance;
    }

    public void setResteSurEcheance(long resteSurEcheance) {
        this.resteSurEcheance = resteSurEcheance;
    }

    public TypeModalite getTypeModalite() {
        return typeModalite;
    }

    public void setTypeModalite(TypeModalite typeModalite) {
        this.typeModalite = typeModalite;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }

    public LocalDate getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDate dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public long getNombreEcheances() {
        return nombreEcheances;
    }

    public void setNombreEcheances(long nombreEcheances) {
        this.nombreEcheances = nombreEcheances;
    }

    public long getMontantParEcheance() {
        return montantParEcheance;
    }

    public void setMontantParEcheance(long montantParEcheance) {
        this.montantParEcheance = montantParEcheance;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public StatutEcheance getStatut() {
        return statut;
    }

    public void setStatut(StatutEcheance statut) {
        this.statut = statut;
    }


}

