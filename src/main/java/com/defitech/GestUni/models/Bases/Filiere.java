package com.defitech.GestUni.models.Bases;

import com.defitech.GestUni.enums.NiveauEtude;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "filieres")
public class Filiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long filiereId;

    private String nomFiliere;
    private String description;

    @ManyToOne
    @JoinColumn(name = "parcours_id", nullable = false)
    private Parcours parcours;

    @ManyToMany(mappedBy = "filieres")
    private List<UE> ue;

    @ManyToMany(mappedBy = "filieres")
    private Set<Etudiant> etudiants = new HashSet<>();

    public Long getFiliereId() {
        return filiereId;
    }

    public void setFiliereId(Long filiereId) {
        this.filiereId = filiereId;
    }

    public String getNomFiliere() {
        return nomFiliere;
    }

    public void setNomFiliere(String nomFiliere) {
        this.nomFiliere = nomFiliere;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Parcours getParcours() {
        return parcours;
    }

    public void setParcours(Parcours parcours) {
        this.parcours = parcours;
    }

    public List<UE> getUe() {
        return ue;
    }

    public void setUe(List<UE> ue) {
        this.ue = ue;
    }

    public Set<Etudiant> getEtudiants() {
        return etudiants;
    }

    public void setEtudiants(Set<Etudiant> etudiants) {
        this.etudiants = etudiants;
    }

    private NiveauEtude niveau; // Assurez-vous d'avoir ce champ

    // Default constructor
    public Filiere() {
    }

    // Parameterized constructor
    public Filiere(String description, NiveauEtude niveau, String nomFiliere, Parcours parcours) {
        this.description = description;
        this.niveau = niveau;
        this.nomFiliere = nomFiliere;
        this.parcours = parcours;
    }

    public NiveauEtude getNiveau() { // Assurez-vous que ce getter existe
        return niveau;
    }
}
