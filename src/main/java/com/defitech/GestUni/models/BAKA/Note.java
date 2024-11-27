package com.defitech.GestUni.models.BAKA;

import com.defitech.GestUni.models.Bases.AnneeScolaire;
import com.defitech.GestUni.models.Bases.Etudiant;
import com.defitech.GestUni.enums.TypeNote;
import com.defitech.GestUni.models.Bases.UE;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;
    private float valeur;
    private TypeNote typeNote;
    private String appreciation;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", insertable = false, updatable = false)
    private Etudiant etudiant;

    @ManyToOne
    @JoinColumn(name = "ue_id", insertable = false, updatable = false)
    private UE ue;

    @ManyToOne
    @JoinColumn(name = "annee_id", insertable = false, updatable = false)
    private AnneeScolaire anneescolaire;
}
