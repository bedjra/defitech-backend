package com.defitech.GestUni.dto.BEDJRA;

import com.defitech.GestUni.enums.BEDJRA.StatutEcheance;
import com.defitech.GestUni.models.BEDJRA.Echeance;
import com.defitech.GestUni.models.BEDJRA.Paiement;
import com.defitech.GestUni.models.Bases.Etudiant;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Data
public class EcheanceDto {
    private Long Id;
    private long montantParEcheance;
    private LocalDate dateEcheance;
    private StatutEcheance statut;
    private long resteSurEcheance;
    private LocalDate dateEnvoi;

    // Constructeur avec paramètres
    public EcheanceDto() {
        this.Id = Id;
        this.montantParEcheance = montantParEcheance;
        this.dateEcheance = dateEcheance;
        this.statut = statut;
        this.resteSurEcheance = resteSurEcheance;
        this.dateEnvoi = dateEnvoi;
    }



    ////////////////////////////getters et setters///////////////////////////////////////

    public static EcheanceDto fromEcheance(Echeance echeance) {
        EcheanceDto dto = new EcheanceDto();
        dto.setId(echeance.getEcheanceId());
        dto.setMontantParEcheance(echeance.getMontantParEcheance());
        dto.setResteSurEcheance(echeance.getResteSurEcheance());
        dto.setDateEcheance(echeance.getDateEcheance());
        dto.setStatut(echeance.getStatut());
        return dto;
    }

    // Ajout d'une méthode statique de création à partir d'une entité Paiement
    public static PaiementDto fromPaiement(Paiement paiement, Etudiant etudiant, List<Echeance> echeances) {
        PaiementDto dto = new PaiementDto();
        // remplissage du DTO
        return dto;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long echeanceId) {
        this.Id = echeanceId;
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

    public long getResteSurEcheance() {
        return resteSurEcheance;
    }

    public void setResteSurEcheance(long resteSurEcheance) {
        this.resteSurEcheance = resteSurEcheance;
    }

    public LocalDate getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDate dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }
}
