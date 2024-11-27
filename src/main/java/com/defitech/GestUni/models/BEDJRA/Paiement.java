package com.defitech.GestUni.models.BEDJRA;

import com.defitech.GestUni.enums.BEDJRA.StatutEcheance;
import com.defitech.GestUni.enums.BEDJRA.StatutScolarite;
import com.defitech.GestUni.enums.BEDJRA.TypeModalite;
import com.defitech.GestUni.models.Bases.Etudiant;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Paiement")
@Data
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate datePaiement;
    private long resteEcolage;
    private long montantDejaPaye;
    private long montantActuel;
    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;

    @OneToMany(mappedBy = "paiement")
    private List<Echeance> echeances;


    public Long getEtudiantId() {
        return etudiant != null ? etudiant.getEtudiantId() : null; // Assurez-vous que la méthode getId() existe dans la classe Etudiant
    }

    public Long getId() {
        return id;
    }

    public void setId(Long paiementId) {
        this.id = paiementId;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public long getMontantActuel() {
        return montantActuel;
    }

    public void setMontantActuel(long montantActuel) {
        this.montantActuel = montantActuel;
    }
    @Enumerated(EnumType.STRING)
    private StatutScolarite statutScolarite;
//    @Enumerated(EnumType.STRING)
//    private StatutEcheance statutEcheance;
////////////getters et setters/////////////////////////////////////////////////////////////////////////////////


    public StatutScolarite getStatutScolarite() {
        return statutScolarite;
    }

    public void setStatutScolarite(StatutScolarite statutScolarite) {
        this.statutScolarite = statutScolarite;
    }

    public long getResteEcolage() {
        return resteEcolage;
    }

    public void setResteEcolage(long resteEcolage) {
        this.resteEcolage = resteEcolage;
    }

    public long getMontantDejaPaye() {
        return montantDejaPaye;
    }

    public void setMontantDejaPaye(long montantDejaPaye) {
        this.montantDejaPaye = montantDejaPaye;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }



    public List<Echeance> getEcheances() {
        return echeances;
    }

    public void setEcheances(List<Echeance> echeances) {
        this.echeances = echeances;
    }

//    public StatutEcheance getStatutEcheance() {
//        return statutEcheance;
//    }
//
//    public void setStatutEcheance(StatutEcheance statutEcheance) {
//        this.statutEcheance = statutEcheance;
//    }






}
