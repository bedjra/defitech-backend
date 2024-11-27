package com.defitech.GestUni.models.Bases;

import com.defitech.GestUni.enums.BEDJRA.MentionBac;
import com.defitech.GestUni.enums.BEDJRA.StatutScolarite;
import com.defitech.GestUni.enums.BEDJRA.Statutboursier;
import com.defitech.GestUni.enums.BEDJRA.TypeModalite;
import com.defitech.GestUni.enums.NiveauEtude;
import com.defitech.GestUni.models.BEDJRA.Echeance;
import com.defitech.GestUni.models.BEDJRA.Paiement;
import com.defitech.GestUni.models.BEDJRA.Reduction;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Etudiant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long etudiantId;
    private String matricule;

    private String nom;
    private String prenom;
    private String email;
    private String adresse;
    private String telephone;
    private LocalDate dateNaiss;
    private String lieuNaiss;
    private String nationnalite;
    private String sexe;
    private String serieBac;
    private String anneeBac;
    private String etatProvenance;
    private LocalDate dateIns;
    private String paysBac;
    private String autreDiplome;

    @Enumerated(EnumType.STRING)
    private Statutboursier statutboursier ;

    @Enumerated(EnumType.STRING)
    private NiveauEtude niveauEtude;

    @Enumerated(EnumType.STRING)
    private MentionBac mentionBac;
    @ManyToOne
    @JoinColumn(name = "parcours_id")
    private Parcours parcours;


    @ManyToMany
    @JoinTable(
            name = "etudiant_filiere",
            joinColumns = @JoinColumn(name = "etudiant_id"),
            inverseJoinColumns = @JoinColumn(name = "filiere_id")
    )
    private Set<Filiere> filieres = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "filiere_id")
    private Filiere filiere;

    @ManyToOne
    @JoinColumn(name = "tuteur_id")
    private Tuteur tuteur;

    @Enumerated(EnumType.STRING)
    private TypeModalite typeModalite;

    @ManyToOne
    @JoinColumn(name = "reduction_id")
    private Reduction reduction ;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Paiement> paiements;



    // Relation avec les échéances
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL)
    private List<Echeance> echeances;

    // Getters et setters pour les champs et la liste d'échéances
    public List<Echeance> getEcheances() {
        return echeances;
    }

    public void setEcheances(List<Echeance> echeances) {
        this.echeances = echeances;
    }
    //////////getters et setters///////////////////////////////////////////////////////////////////////////////




    public Long getEtudiantId() {
        return etudiantId;
    }

    public void setEtudiantId(Long etudiantId) {
        this.etudiantId = etudiantId;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getDateNaiss() {
        return dateNaiss;
    }

    public void setDateNaiss(LocalDate dateNaiss) {
        this.dateNaiss = dateNaiss;
    }

    public String getLieuNaiss() {
        return lieuNaiss;
    }

    public void setLieuNaiss(String lieuNaiss) {
        this.lieuNaiss = lieuNaiss;
    }

    public String getNationnalite() {
        return nationnalite;
    }

    public void setNationnalite(String nationnalite) {
        this.nationnalite = nationnalite;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getSerieBac() {
        return serieBac;
    }

    public void setSerieBac(String serieBac) {
        this.serieBac = serieBac;
    }

    public String getAnneeBac() {
        return anneeBac;
    }

    public void setAnneeBac(String anneeBac) {
        this.anneeBac = anneeBac;
    }

    public String getEtatProvenance() {
        return etatProvenance;
    }

    public void setEtatProvenance(String etatProvenance) {
        this.etatProvenance = etatProvenance;
    }

    public LocalDate getDateIns() {
        return dateIns;
    }

    public void setDateIns(LocalDate dateIns) {
        this.dateIns = dateIns;
    }

    public String getPaysBac() {
        return paysBac;
    }

    public void setPaysBac(String paysBac) {
        this.paysBac = paysBac;
    }

    public String getAutreDiplome() {
        return autreDiplome;
    }

    public void setAutreDiplome(String autreDiplome) {
        this.autreDiplome = autreDiplome;
    }

    public Statutboursier getStatutboursier() {
        return statutboursier;
    }

    public void setStatutboursier(Statutboursier statutboursier) {
        this.statutboursier = statutboursier;
    }

    public NiveauEtude getNiveauEtude() {
        return niveauEtude;
    }

    public void setNiveauEtude(NiveauEtude niveauEtude) {
        this.niveauEtude = niveauEtude;
    }

    public MentionBac getMentionBac() {
        return mentionBac;
    }

    public void setMentionBac(MentionBac mentionBac) {
        this.mentionBac = mentionBac;
    }

    public Parcours getParcours() {
        return parcours;
    }

    public void setParcours(Parcours parcours) {
        this.parcours = parcours;
    }

    public Set<Filiere> getFilieres() {
        return filieres;
    }

    public void setFilieres(Set<Filiere> filieres) {
        this.filieres = filieres;
    }

    public Filiere getFiliere() {
        return filiere;
    }

    public void setFiliere(Filiere filiere) {
        this.filiere = filiere;
    }

    public Tuteur getTuteur() {
        return tuteur;
    }

    public void setTuteur(Tuteur tuteur) {
        this.tuteur = tuteur;
    }

    public TypeModalite getTypeModalite() {
        return typeModalite;
    }

    public void setTypeModalite(TypeModalite typeModalite) {
        this.typeModalite = typeModalite;
    }

    public Reduction getReduction() {
        return reduction;
    }

    public void setReduction(Reduction reduction) {
        this.reduction = reduction;
    }

    public List<Paiement> getPaiements() {
        return paiements;
    }

    public void setPaiements(List<Paiement> paiements) {
        this.paiements = paiements;
    }



}
