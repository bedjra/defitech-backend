package com.defitech.GestUni.dto.BEDJRA;



import com.defitech.GestUni.enums.BEDJRA.StatutScolarite;
import com.defitech.GestUni.enums.NiveauEtude;
import com.defitech.GestUni.enums.BEDJRA.StatutEcheance;
import com.defitech.GestUni.enums.BEDJRA.TypeModalite;
import com.defitech.GestUni.models.BEDJRA.Echeance;
import com.defitech.GestUni.models.BEDJRA.Paiement;
import com.defitech.GestUni.models.Bases.Etudiant;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PaiementDto {
    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantMatricule;
    private String filiereNom;
    private String parcoursNom;
    private long reductionMontantFinal;
    private NiveauEtude niveauEtude;
    private long montantDejaPaye;
    private TypeModalite typeModalite;
    private LocalDate datePaiement;
    private long montantActuel;
    private long resteEcolage;
    private long montantAChanger;
    private List<EcheanceDto> echeances;
    private StatutScolarite StatutScolarite ;
    private String TuteurMail;

    public void setStatutScolarite(com.defitech.GestUni.enums.BEDJRA.StatutScolarite statutScolarite) {
        StatutScolarite = statutScolarite;
    }

    public String getTuteurMail() {
        return TuteurMail;
    }

    public void setTuteurMail(String tuteurMail) {
        TuteurMail = tuteurMail;
    }

    public List<EcheanceDto> getEcheances() {
        return echeances;
    }

    public void updateStatutEcheances(Long montantTotalPaye) {
        for (EcheanceDto echeance : echeances) {
            if (montantTotalPaye >= echeance.getMontantParEcheance()) {
                echeance.setStatut(StatutEcheance.PAYEE);
                montantTotalPaye -= echeance.getMontantParEcheance();
            } else if (montantTotalPaye > 0) {
                echeance.setStatut(StatutEcheance.PARTIELLEMENT_PAYEE);
                montantTotalPaye = 0L;
            } else {
                echeance.setStatut(StatutEcheance.EN_ATTENTE);
            }
        }
    }

    // Getters and Setters


    public long getMontantAChanger() {
        return montantAChanger;
    }

    public void setMontantAChanger(long montantAChanger) {
        this.montantAChanger = montantAChanger;
    }

    public String getEtudiantMatricule() {
        return etudiantMatricule;
    }

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

    public String getEtudiantMatricule(String matricule) {
        return etudiantMatricule;
    }

    public void setEtudiantMatricule(String etudiantMatricule) {
        this.etudiantMatricule = etudiantMatricule;
    }

    public String getFiliereNom() {
        return filiereNom;
    }

    public void setFiliereNom(String filiereNom) {
        this.filiereNom = filiereNom;
    }

    public String getParcoursNom() {
        return parcoursNom;
    }

    public void setParcoursNom(String parcoursNom) {
        this.parcoursNom = parcoursNom;
    }

    public long getReductionMontantFinal() {
        return reductionMontantFinal;
    }

    public void setReductionMontantFinal(long reductionMontantFinal) {
        this.reductionMontantFinal = reductionMontantFinal;
    }

    //////////////////getters et setters/////////////////////////////////////////////


    public com.defitech.GestUni.enums.BEDJRA.StatutScolarite getStatutScolarite() {
        return StatutScolarite;
    }

    public NiveauEtude getNiveauEtude() {
        return niveauEtude;
    }

    public void setNiveauEtude(NiveauEtude niveauEtude) {
        this.niveauEtude = niveauEtude;
    }

    public long getMontantDejaPaye() {
        return montantDejaPaye;
    }

    public void setMontantDejaPaye(long montantDejaPaye) {
        this.montantDejaPaye = montantDejaPaye;
    }

    public TypeModalite getTypeModalite() {
        return typeModalite;
    }

    public void setTypeModalite(TypeModalite typeModalite) {
        this.typeModalite = typeModalite;
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

    public long getResteEcolage() {
        return resteEcolage;
    }

    public void setResteEcolage(long resteEcolage) {
        this.resteEcolage = resteEcolage;
    }

    public void setEcheances(List<EcheanceDto> echeances) {
        this.echeances = echeances;
    }

    // Ajout d'une méthode statique de création à partir d'une entité Paiement
    public static PaiementDto fromPaiement(Paiement paiement, Etudiant etudiant, List<Echeance> echeances) {
        PaiementDto dto = new PaiementDto();
        dto.setEtudiantNom(etudiant.getNom());
        dto.setEtudiantPrenom(etudiant.getPrenom());
        dto.setTuteurMail(etudiant.getTuteur().getEmail());
        dto.setResteEcolage(paiement.getResteEcolage());

        // Transformer les entités Echeance en EcheanceDto et les ajouter au DTO
        List<EcheanceDto> echeanceDtos = echeances.stream()
                .map(EcheanceDto::fromEcheance)
                .collect(Collectors.toList());
        dto.setEcheances(echeanceDtos);

        return dto;
    }


}