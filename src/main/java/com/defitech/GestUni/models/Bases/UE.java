package com.defitech.GestUni.models.Bases;

import com.defitech.GestUni.enums.NiveauEtude;
import com.defitech.GestUni.enums.TypeSemestre;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class UE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ueId;

    private String libelle;
    private String code;
    private String descriptUe;
    
    private int credit;

    private String typeUe;

    @Enumerated(EnumType.STRING)
    private NiveauEtude niveauEtude;

    @Enumerated(EnumType.STRING)
    private TypeSemestre typeSemestre;

    @ManyToOne
    @JoinColumn(name = "professeur_id", insertable = false, updatable = false)
    private Professeur professeur;

    @ManyToMany
    @JoinTable(
            name = "ue_filiere",
            joinColumns = @JoinColumn(name = "ue_id"),
            inverseJoinColumns = @JoinColumn(name = "filiere_id")
    )
    private List<Filiere> filieres;

}
