package com.defitech.GestUni.dto.BEDJRA;

import com.defitech.GestUni.enums.BEDJRA.Statutboursier;
import com.defitech.GestUni.enums.BEDJRA.MentionBac;
import com.defitech.GestUni.enums.BEDJRA.TypeModalite;
import com.defitech.GestUni.enums.NiveauEtude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentDto {

    private String etudiantMatricule;

    @NotEmpty(message = "Le nom de l'étudiant est requis")
    private String etudiantNom;

    @Email(message = "L'email doit être valide")
    @NotEmpty(message = "L'email de l'étudiant est requis")
    private String etudiantMail;

    @NotEmpty(message = "Le prénom de l'étudiant est requis")
    private String etudiantPrenom;

    @NotEmpty(message = "L'adresse de l'étudiant est requise")
    private String etudiantAdresse;

    @NotEmpty(message = "Le numéro de téléphone de l'étudiant est requis")
    private String etudiantTelephone;

    @NotNull(message = "La date de naissance ne peut pas être vide")
    private LocalDate etudiantDateNaiss;

    @NotEmpty(message = "Le lieu de naissance est requis")
    private String etudiantLieuNais;

    @NotEmpty(message = "La nationalité est requise")
    private String etudiantNationnalite;

    @NotEmpty(message = "Le sexe est requis")
    private String etudiantSexe;  // Changer de char à String pour plus de flexibilité

    @NotEmpty(message = "La série du bac est requise")
    private String etudiantSerieBac;  // Changer de char à String pour plus de flexibilité

    @NotEmpty(message = "L'année du bac est requise")
    private String etudiantAnneeBac;

    @NotEmpty(message = "L'état de provenance est requis")
    private String etudiantEtatProvenance;

    @NotNull(message = "La date d'inscription ne peut pas être vide")
    private LocalDate etudiantDateIns;

    @NotEmpty(message = "Le pays du bac est requis")
    private String etudiantPaysBac;

    private String etudiantAutreDiplome;

    @NotEmpty(message = "Le nom du tuteur est requis")
    private String tuteurNom;

    @NotEmpty(message = "Le prénom du tuteur est requis")
    private String tuteurPrenom;

    @NotEmpty(message = "La profession du tuteur est requise")
    private String tuteurProfession;

    @NotEmpty(message = "L'organisme employeur du tuteur est requis")
    private String tuteurOrganismeEmployeur;

    @NotEmpty(message = "L'adresse du tuteur est requise")
    private String tuteurAdresse;

    @NotEmpty(message = "Le téléphone de bureau du tuteur est requis")
    private String tuteurTelBureau; // Correction du nom du champ

    private String tuteurTelDom;

    @NotEmpty(message = "Le téléphone portable du tuteur est requis")
    private String tuteurCel;

    @Email(message = "L'email du tuteur doit être valide")
    private String tuteurEmail;

    private MentionBac mentionBac;

    @NotNull(message = "Le niveau d'étude est requis")
    private NiveauEtude niveauEtude;

    @NotNull(message = "Le type de modalité est requis")
    private TypeModalite typeModalite;

    @NotNull(message = "Le statut boursier est requis")
    private Statutboursier boursier;  // Changer de boolean à String pour correspondre à "Oui", "Non", "Compassion"

    private long reductionMontantFinal;

    @NotNull(message = "Le parcours est requis")
    private Long parcoursId;

    private String nomParcours;

    @NotEmpty(message = "Le nom de la filière est requis")
    private String nomFiliere;

    public void afficherInformations() {
        System.out.println("Matricule : " + etudiantMatricule);
        System.out.println("Nom : " + etudiantNom);
        System.out.println("Prénom : " + etudiantPrenom);
        System.out.println("Mail : " + etudiantMail);
        System.out.println("Adresse : " + etudiantAdresse);
        System.out.println("Téléphone : " + etudiantTelephone);
        System.out.println("Date de Naissance : " + etudiantDateNaiss);
        System.out.println("Lieu de Naissance : " + etudiantLieuNais);
        System.out.println("Nationalité : " + etudiantNationnalite);
        System.out.println("Sexe : " + etudiantSexe);
        System.out.println("Série Bac : " + etudiantSerieBac);
        System.out.println("Année Bac : " + etudiantAnneeBac);
        System.out.println("État Provenance : " + etudiantEtatProvenance);
        System.out.println("Date d'Inscription : " + etudiantDateIns);
        System.out.println("Pays Bac : " + etudiantPaysBac);
        System.out.println("Autre Diplôme : " + etudiantAutreDiplome);

        System.out.println("Tuteur Nom : " + tuteurNom);
        System.out.println("Tuteur Prénom : " + tuteurPrenom);
        System.out.println("Tuteur Profession : " + tuteurProfession);
        System.out.println("Organisme Employeur : " + tuteurOrganismeEmployeur);
        System.out.println("Adresse Tuteur : " + tuteurAdresse);
        System.out.println("Téléphone Bureau Tuteur : " + tuteurTelBureau);
        System.out.println("Téléphone Domicile Tuteur : " + tuteurTelDom);
        System.out.println("Téléphone Portable Tuteur : " + tuteurCel);
        System.out.println("Email Tuteur : " + tuteurEmail);

        System.out.println("Mention Bac : " + mentionBac);
        System.out.println("Niveau d'Étude : " + niveauEtude);
        System.out.println("Type Modalité : " + typeModalite);
        System.out.println("Statut Boursier : " + boursier);

        System.out.println("ID Parcours : " + parcoursId);
        System.out.println("Nom Parcours : " + nomParcours);
        System.out.println("Nom Filière : " + nomFiliere);

        System.out.println("MontantFinal : " + reductionMontantFinal);

    }


    public void InformationsEtudiant() {
        System.out.println("Matricule : " + etudiantMatricule);
        System.out.println("Nom : " + etudiantNom);
        System.out.println("Prénom : " + etudiantPrenom);
        System.out.println("Téléphone : " + etudiantTelephone);
        System.out.println("Sexe : " + etudiantSexe);
        
        System.out.println("MontantFinal : " + reductionMontantFinal);

    }

    public String getEtudiantMatricule() {
        return etudiantMatricule;
    }

    public void setEtudiantMatricule(String etudiantMatricule) {
        this.etudiantMatricule = etudiantMatricule;
    }

    public String getEtudiantNom() {
        return etudiantNom;
    }

    public void setEtudiantNom(String etudiantNom) {
        this.etudiantNom = etudiantNom;
    }

    public String getEtudiantMail() {
        return etudiantMail;
    }

    public void setEtudiantMail(String etudiantMail) {
        this.etudiantMail = etudiantMail;
    }

    public String getEtudiantPrenom() {
        return etudiantPrenom;
    }

    public void setEtudiantPrenom(String etudiantPrenom) {
        this.etudiantPrenom = etudiantPrenom;
    }

    public String getEtudiantAdresse() {
        return etudiantAdresse;
    }

    public void setEtudiantAdresse(String etudiantAdresse) {
        this.etudiantAdresse = etudiantAdresse;
    }

    public String getEtudiantTelephone() {
        return etudiantTelephone;
    }

    public void setEtudiantTelephone(String etudiantTelephone) {
        this.etudiantTelephone = etudiantTelephone;
    }

    public LocalDate getEtudiantDateNaiss() {
        return etudiantDateNaiss;
    }

    public void setEtudiantDateNaiss(LocalDate etudiantDateNaiss) {
        this.etudiantDateNaiss = etudiantDateNaiss;
    }

    public String getEtudiantLieuNais() {
        return etudiantLieuNais;
    }

    public void setEtudiantLieuNais(String etudiantLieuNais) {
        this.etudiantLieuNais = etudiantLieuNais;
    }

    public String getEtudiantNationnalite() {
        return etudiantNationnalite;
    }

    public void setEtudiantNationnalite(String etudiantNationnalite) {
        this.etudiantNationnalite = etudiantNationnalite;
    }

    public String getEtudiantSexe() {
        return etudiantSexe;
    }

    public void setEtudiantSexe(String etudiantSexe) {
        this.etudiantSexe = etudiantSexe;
    }

    public String getEtudiantSerieBac() {
        return etudiantSerieBac;
    }

    public void setEtudiantSerieBac(String etudiantSerieBac) {
        this.etudiantSerieBac = etudiantSerieBac;
    }

    public String getEtudiantAnneeBac() {
        return etudiantAnneeBac;
    }

    public void setEtudiantAnneeBac(String etudiantAnneeBac) {
        this.etudiantAnneeBac = etudiantAnneeBac;
    }

    public String getEtudiantEtatProvenance() {
        return etudiantEtatProvenance;
    }

    public void setEtudiantEtatProvenance(String etudiantEtatProvenance) {
        this.etudiantEtatProvenance = etudiantEtatProvenance;
    }

    public LocalDate getEtudiantDateIns() {
        return etudiantDateIns;
    }

    public void setEtudiantDateIns(LocalDate etudiantDateIns) {
        this.etudiantDateIns = etudiantDateIns;
    }

    public String getEtudiantPaysBac() {
        return etudiantPaysBac;
    }

    public void setEtudiantPaysBac(String etudiantPaysBac) {
        this.etudiantPaysBac = etudiantPaysBac;
    }

    public String getEtudiantAutreDiplome() {
        return etudiantAutreDiplome;
    }

    public void setEtudiantAutreDiplome(String etudiantAutreDiplome) {
        this.etudiantAutreDiplome = etudiantAutreDiplome;
    }

    public String getTuteurNom() {
        return tuteurNom;
    }

    public void setTuteurNom(String tuteurNom) {
        this.tuteurNom = tuteurNom;
    }

    public String getTuteurPrenom() {
        return tuteurPrenom;
    }

    public void setTuteurPrenom(String tuteurPrenom) {
        this.tuteurPrenom = tuteurPrenom;
    }

    public String getTuteurProfession() {
        return tuteurProfession;
    }

    public void setTuteurProfession(String tuteurProfession) {
        this.tuteurProfession = tuteurProfession;
    }

    public String getTuteurOrganismeEmployeur() {
        return tuteurOrganismeEmployeur;
    }

    public void setTuteurOrganismeEmployeur(String tuteurOrganismeEmployeur) {
        this.tuteurOrganismeEmployeur = tuteurOrganismeEmployeur;
    }

    public String getTuteurAdresse() {
        return tuteurAdresse;
    }

    public void setTuteurAdresse(String tuteurAdresse) {
        this.tuteurAdresse = tuteurAdresse;
    }

    public String getTuteurTelBureau() {
        return tuteurTelBureau;
    }

    public void setTuteurTelBureau(String tuteurTelBureau) {
        this.tuteurTelBureau = tuteurTelBureau;
    }

    public String getTuteurTelDom() {
        return tuteurTelDom;
    }

    public void setTuteurTelDom(String tuteurTelDom) {
        this.tuteurTelDom = tuteurTelDom;
    }

    public String getTuteurCel() {
        return tuteurCel;
    }

    public void setTuteurCel(String tuteurCel) {
        this.tuteurCel = tuteurCel;
    }

    public String getTuteurEmail() {
        return tuteurEmail;
    }

    public void setTuteurEmail(String tuteurEmail) {
        this.tuteurEmail = tuteurEmail;
    }

    public MentionBac getMentionBac() {
        return mentionBac;
    }

    public void setMentionBac(MentionBac mentionBac) {
        this.mentionBac = mentionBac;
    }

    public NiveauEtude getNiveauEtude() {
        return niveauEtude;
    }

    public void setNiveauEtude(NiveauEtude niveauEtude) {
        this.niveauEtude = niveauEtude;
    }

    public TypeModalite getTypeModalite() {
        return typeModalite;
    }

    public void setTypeModalite(TypeModalite typeModalite) {
        this.typeModalite = typeModalite;
    }

    public Statutboursier getBoursier() {
        return boursier;
    }

    public void setBoursier(Statutboursier boursier) {
        this.boursier = boursier;
    }

    public long getReductionMontantFinal() {
        return reductionMontantFinal;
    }

    public void setReductionMontantFinal(long reductionMontantFinal) {
        this.reductionMontantFinal = reductionMontantFinal;
    }

    public Long getParcoursId() {
        return parcoursId;
    }

    public void setParcoursId(Long parcoursId) {
        this.parcoursId = parcoursId;
    }

    public String getNomParcours() {
        return nomParcours;
    }

    public void setNomParcours(String nomParcours) {
        this.nomParcours = nomParcours;
    }

    public String getNomFiliere() {
        return nomFiliere;
    }

    public void setNomFiliere(String nomFiliere) {
        this.nomFiliere = nomFiliere;
    }
}
