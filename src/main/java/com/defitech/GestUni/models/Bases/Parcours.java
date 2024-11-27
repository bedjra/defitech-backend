package com.defitech.GestUni.models.Bases;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Parcours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parcoursId;

    private long montantScolarite;
    private String nomParcours;

    public Long getParcoursId() {
        return parcoursId;
    }

    public void setParcoursId(Long parcoursId) {
        this.parcoursId = parcoursId;
    }

    public long getMontantScolarite() {
        return montantScolarite;
    }

    public void setMontantScolarite(long montantScolarite) {
        this.montantScolarite = montantScolarite;
    }

    public String getNomParcours() {
        return nomParcours;
    }

    public void setNomParcours(String nomParcours) {
        this.nomParcours = nomParcours;
    }
}
