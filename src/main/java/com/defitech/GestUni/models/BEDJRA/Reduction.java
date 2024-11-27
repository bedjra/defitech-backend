package com.defitech.GestUni.models.BEDJRA;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "reduction")
public class Reduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reductionId;
    private String nom;
    private long pourcentage;
    private long montantFinal;


    public Long getReductionId() {
        return reductionId;
    }

    public void setReductionId(Long reductionId) {
        this.reductionId = reductionId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(long pourcentage) {
        this.pourcentage = pourcentage;
    }

    public long getMontantFinal() {
        return montantFinal;
    }

    public void setMontantFinal(long montantFinal) {
        this.montantFinal = montantFinal;
    }
}
