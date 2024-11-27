package com.defitech.GestUni.service.BEDJRA;

import com.defitech.GestUni.dto.BEDJRA.*;
import com.defitech.GestUni.enums.BEDJRA.StatutEcheance;
import com.defitech.GestUni.enums.BEDJRA.StatutScolarite;
import com.defitech.GestUni.enums.BEDJRA.TypeModalite;
import com.defitech.GestUni.enums.NiveauEtude;
import com.defitech.GestUni.models.BEDJRA.Echeance;
import com.defitech.GestUni.models.BEDJRA.Paiement;
import com.defitech.GestUni.models.BEDJRA.Reduction;
import com.defitech.GestUni.models.Bases.Etudiant;
import com.defitech.GestUni.models.Bases.Filiere;
import com.defitech.GestUni.models.Bases.Tuteur;
import com.defitech.GestUni.repository.BEDJRA.EcheanceRepository;
import com.defitech.GestUni.repository.BEDJRA.PaiementRepository;
import com.defitech.GestUni.repository.BEDJRA.ReductionRepository;
import com.defitech.GestUni.repository.BEDJRA.StudentRepository;
import com.defitech.GestUni.repository.EtudiantRepository;
import com.defitech.GestUni.repository.FiliereRepository;
import com.defitech.GestUni.repository.ParcoursRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PaiementService {

    @Autowired
    private EmailService emailService;
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ParcoursRepository parcoursRepository;

    @Autowired
    private ReductionRepository reductionRepository;


    @Autowired
    private EtudiantRepository etudiantRepository;
    @Autowired
    private EcheanceRepository echeanceRepository;
    @Autowired
    private PaiementRepository paiementRepository;
    @Autowired
    private FiliereRepository filiereRepository;


    //////////////////////////get NMBRE TOTAL ///////
    public long getTotalEtudiants() {
        return etudiantRepository.countTotalEtudiants();
    }

    // Compter le nombre d'étudiants dont le reste à payer est 0 (soldé)
    public long compterEtudiantsSoldes() {
        return paiementRepository.countEtudiantsSoldes();
    }

    // Méthode pour obtenir le nombre d'étudiants n'ayant pas encore soldé
    public long compterEtudiantsNonSoldes() {
        long totalEtudiants = getTotalEtudiants();
        long etudiantsSoldes = compterEtudiantsSoldes();
        return totalEtudiants - etudiantsSoldes;
    }


    public List<Etudiant> searchByNomOrPrenom(String searchTerm) {
        return etudiantRepository.findByNomStartingWithOrPrenomStartingWith(searchTerm, searchTerm);
    }


    public List<Etudiant> findByPaiementDto(PaiementDto paiementDto) {
        return etudiantRepository.findByNomAndPrenom(paiementDto.getEtudiantNom(), paiementDto.getEtudiantPrenom());
    }



    @Transactional
    public PaiementDto effectuerPaiement(PaiementDto paiementDto) {
        // Vérifier si montantActuel est négatif
        long montantActuel = paiementDto.getMontantActuel();
        if (montantActuel < 0) {
            throw new RuntimeException("Le montant actuel ne peut pas être négatif.");
        }

        // Récupérer l'étudiant
        List<Etudiant> etudiants = etudiantRepository.findByNomAndPrenom(paiementDto.getEtudiantNom(), paiementDto.getEtudiantPrenom());
        if (etudiants.isEmpty()) {
            throw new RuntimeException("Aucun étudiant trouvé avec les nom et prénom spécifiés.");
        }
        Etudiant etudiant = etudiants.get(0);

        // Calculer le montant déjà payé avant ce paiement
        List<Paiement> paiements = paiementRepository.findByEtudiant(etudiant);
        long montantDejaPaye = paiements.stream().mapToLong(Paiement::getMontantActuel).sum();

        // Ajouter le montant actuel payé
        montantDejaPaye += montantActuel;

        // Calculer le montant final
        Reduction reduction = etudiant.getReduction();
        long montantFinal = (reduction != null) ? reduction.getMontantFinal() : 0;

        // Calculer le reste à payer
        long resteEcolage = montantFinal - montantDejaPaye;

        // S'assurer que resteEcolage ne devient pas négatif
        resteEcolage = Math.max(resteEcolage, 0);

        // Vérifier si montantActuel dépasse resteEcolage
        if (montantActuel > montantFinal) {
            throw new RuntimeException("Le montant ne peut pas dépasser le reste à payer.");
        }

        // Sauvegarder le paiement
        Paiement paiement = new Paiement();
        paiement.setEtudiant(etudiant);
        paiement.setMontantActuel(montantActuel);
        paiement.setDatePaiement(paiementDto.getDatePaiement());
        paiement.setMontantDejaPaye(montantDejaPaye);
        paiement.setResteEcolage(resteEcolage);

        paiementRepository.save(paiement);

        // Déterminer le statut de scolarité
        paiement.setStatutScolarite(resteEcolage == 0 ? StatutScolarite.SOLDE : StatutScolarite.EN_COURS);

        // Générer les échéances
        TypeModalite modalite = etudiant.getTypeModalite();
        Integer nombreEcheances = obtenirNombreEcheancesPourModalite(modalite);
        LocalDate dateFinEcheance = LocalDate.now().withMonth(11).withDayOfMonth(14);

        List<EcheanceDto> echeanceDTOs = new ArrayList<>();
        long montantRestant = montantDejaPaye;

        for (int i = 0; i < nombreEcheances; i++) {
            EcheanceDto echeanceDto = new EcheanceDto();
            echeanceDto.setId((long) (i + 1));
            long montantPourCetteEcheance = (i == 0) ? montantFinal / nombreEcheances : montantFinal / nombreEcheances;
            echeanceDto.setMontantParEcheance(montantPourCetteEcheance);
            echeanceDto.setDateEcheance(calculerProchaineDateEcheance(i, modalite, dateFinEcheance));
            echeanceDto.setDateEnvoi(echeanceDto.getDateEcheance().minusWeeks(2));

            // Calcul du reste sur chaque échéance
            echeanceDto.setResteSurEcheance(Math.max(montantPourCetteEcheance - montantRestant, 0));

            // Vérifier l'état de chaque échéance
            if (montantRestant >= montantPourCetteEcheance) {
                echeanceDto.setStatut(StatutEcheance.PAYEE);
                montantRestant -= montantPourCetteEcheance;
            } else if (montantRestant > 0) {
                echeanceDto.setStatut(StatutEcheance.PARTIELLEMENT_PAYEE);
                montantRestant = 0;
            } else {
                echeanceDto.setStatut(StatutEcheance.EN_ATTENTE);
            }

            echeanceDTOs.add(echeanceDto);
        }

        // Récupérer le tuteur
        Tuteur tuteur = etudiant.getTuteur();
        if (tuteur != null && tuteur.getEmail() != null && !tuteur.getEmail().isEmpty()) {
            String tuteurMail = tuteur.getEmail();
            String subject = "Notification de Paiement pour " + etudiant.getNom() + " " + etudiant.getPrenom();

            // Contenu de l'email à envoyer
            String text = "Bonjour,\n\n" +
                    "Nous vous informons que le paiement d'un montant de " + montantActuel + " a été effectué pour l'étudiant " +
                    etudiant.getNom() + " " + etudiant.getPrenom() + " le " + paiementDto.getDatePaiement() + ".\n" +
                    "Montant déjà payé : " + montantDejaPaye + "\n" +
                    "Reste à payer : " + resteEcolage + "\n\n" +
                    "Cordialement,\nL'administration.";

            // Envoi de l'email
            emailService.sendEmail(tuteurMail, subject, text);
        } else {
            System.out.println("Erreur : Le tuteur ou son adresse email n'est pas défini(e).");
        }

        String tuteurMail = (tuteur != null) ? tuteur.getEmail() : null;


        // Préparer la réponse
        PaiementDto paiementResponse = new PaiementDto();
        paiementResponse.setEtudiantId(etudiant.getEtudiantId());
        paiementResponse.setEtudiantNom(etudiant.getNom());
        paiementResponse.setEtudiantPrenom(etudiant.getPrenom());
        paiementResponse.setEtudiantMatricule(etudiant.getMatricule());
        paiementResponse.setFiliereNom(etudiant.getFiliere().getNomFiliere());
        paiementResponse.setParcoursNom(etudiant.getParcours().getNomParcours());
        paiementResponse.setReductionMontantFinal(montantFinal);
        paiementResponse.setNiveauEtude(etudiant.getNiveauEtude());
        paiementResponse.setMontantDejaPaye(montantDejaPaye);
        paiementResponse.setDatePaiement(paiementDto.getDatePaiement());
        paiementResponse.setMontantActuel(montantActuel);
        paiementResponse.setResteEcolage(resteEcolage);
        paiementResponse.setEcheances(echeanceDTOs);
        paiementResponse.setTuteurMail(tuteurMail);
        paiementResponse.setStatutScolarite(resteEcolage == 0 ? StatutScolarite.SOLDE : StatutScolarite.EN_COURS);

        return paiementResponse;
    }

    @Transactional
    public PaiementDto getPaiementByEtudiantMatricule(String etudiantMatricule) {
        // Récupérer l'étudiant à partir du matricule
        Etudiant etudiant = etudiantRepository.findByMatricule(etudiantMatricule)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable avec le matricule : " + etudiantMatricule));

        // Récupérer tous les paiements associés à cet étudiant
        List<Paiement> paiements = paiementRepository.findByEtudiant(etudiant);
        if (paiements.isEmpty()) {
            throw new RuntimeException("Aucun paiement trouvé pour l'étudiant avec le matricule : " + etudiantMatricule);
        }

        // Utiliser le dernier paiement effectué pour les informations principales
        Paiement dernierPaiement = paiements.get(paiements.size() - 1);

        // Calculer le montant déjà payé par l'étudiant
        long montantDejaPaye = paiements.stream().mapToLong(Paiement::getMontantActuel).sum();

        // Récupérer la réduction et calculer le montant final
        Reduction reduction = etudiant.getReduction();
        long montantFinal = (reduction != null) ? reduction.getMontantFinal() : 0;

        // Calculer le reste à payer
        long resteEcolage = montantFinal - montantDejaPaye;

        // S'assurer que resteEcolage ne devient pas négatif
        resteEcolage = Math.max(resteEcolage, 0);

        // Calculer le nombre d'échéances
        TypeModalite modalite = etudiant.getTypeModalite();
        Integer nombreEcheances = obtenirNombreEcheancesPourModalite(modalite);

        // Date de début ajustée au 14 NOVEMBRE de l'année en cours
        LocalDate dateFinEcheance = LocalDate.now().withMonth(11).withDayOfMonth(14);

        // Créer les échéances
        List<EcheanceDto> echeanceDTOs = new ArrayList<>();
        long montantRestant = montantDejaPaye;

        if (nombreEcheances == 1) {
            EcheanceDto echeanceDto = new EcheanceDto();
            echeanceDto.setId(1L);
            echeanceDto.setMontantParEcheance(montantFinal);
            echeanceDto.setDateEcheance(dateFinEcheance);
            echeanceDto.setDateEnvoi(dateFinEcheance.minusWeeks(2));
            echeanceDto.setResteSurEcheance(montantFinal - montantRestant);

            // Déterminer le statut de l'échéance
            if (montantDejaPaye >= montantFinal) {
                echeanceDto.setStatut(StatutEcheance.PAYEE);
            } else if (montantDejaPaye > 0) {
                echeanceDto.setStatut(StatutEcheance.PARTIELLEMENT_PAYEE);
            } else {
                echeanceDto.setStatut(StatutEcheance.EN_ATTENTE);
            }

            echeanceDTOs.add(echeanceDto);
        } else {
            long montantSuppl = 10000;
            long montantParEcheance = (montantFinal - montantSuppl) / (nombreEcheances - 1);

            for (int i = 0; i < nombreEcheances; i++) {
                EcheanceDto echeanceDto = new EcheanceDto();
                echeanceDto.setId(Long.valueOf(i + 1));

                long montantPourCetteEcheance = (i == 0) ? montantParEcheance + montantSuppl : montantParEcheance;
                echeanceDto.setMontantParEcheance(montantPourCetteEcheance);
                echeanceDto.setDateEcheance(calculerProchaineDateEcheance(i, modalite, dateFinEcheance));
                echeanceDto.setDateEnvoi(echeanceDto.getDateEcheance().minusWeeks(2));

                echeanceDto.setResteSurEcheance(Math.max(montantPourCetteEcheance - montantRestant, 0));

                // Vérification de l'état de l'échéance
                if (montantRestant >= montantPourCetteEcheance) {
                    echeanceDto.setStatut(StatutEcheance.PAYEE);
                    montantRestant -= montantPourCetteEcheance;
                } else if (montantRestant > 0) {
                    echeanceDto.setStatut(StatutEcheance.PARTIELLEMENT_PAYEE);
                    montantRestant = 0;
                } else {
                    echeanceDto.setStatut(StatutEcheance.EN_ATTENTE);
                }

                echeanceDTOs.add(echeanceDto);
            }
        }

        for (EcheanceDto echeanceDto : echeanceDTOs) {
            Echeance echeance = new Echeance();
            echeance.setEtudiant(etudiant); // Associe l'échéance à l'étudiant
            echeance.setMontantParEcheance(echeanceDto.getMontantParEcheance());
            echeance.setDateEcheance(echeanceDto.getDateEcheance());
            echeance.setDateEnvoi(echeanceDto.getDateEnvoi());
            echeance.setResteSurEcheance(echeanceDto.getResteSurEcheance());
            echeance.setStatut(echeanceDto.getStatut());

            echeanceRepository.save(echeance); // Enregistre l'échéance dans la base de données
        }

        // Récupérer le tuteur et son email
        Tuteur tuteur = etudiant.getTuteur();
        String tuteurMail = (tuteur != null) ? tuteur.getEmail() : null;

        // Préparer la réponse
        PaiementDto paiementResponse = new PaiementDto();
        paiementResponse.setEtudiantId(etudiant.getEtudiantId());
        paiementResponse.setEtudiantNom(etudiant.getNom());
        paiementResponse.setEtudiantPrenom(etudiant.getPrenom());
        paiementResponse.setEtudiantMatricule(etudiant.getMatricule());
        paiementResponse.setFiliereNom(etudiant.getFiliere().getNomFiliere());
        paiementResponse.setParcoursNom(etudiant.getParcours().getNomParcours());
        paiementResponse.setReductionMontantFinal(montantFinal);
        paiementResponse.setNiveauEtude(etudiant.getNiveauEtude());
        paiementResponse.setMontantDejaPaye(montantDejaPaye);
        paiementResponse.setTypeModalite(TypeModalite.valueOf(modalite.name()));
        paiementResponse.setDatePaiement(dernierPaiement.getDatePaiement());
        paiementResponse.setMontantActuel(dernierPaiement.getMontantActuel());
        paiementResponse.setResteEcolage(resteEcolage);
        paiementResponse.setEcheances(echeanceDTOs);
        paiementResponse.setTuteurMail(tuteurMail);
        paiementResponse.setStatutScolarite((resteEcolage == 0) ? StatutScolarite.SOLDE : StatutScolarite.EN_COURS);

        return paiementResponse;
    }


    private Integer obtenirNombreEcheancesPourModalite(TypeModalite typeModalite) {
        switch (typeModalite) {
            case TOTALITE:
                return 1;
            case TROIS_TRANCHES:
                return 3;
            case SEPT_TRANCHES:
                return 7;
            default:
                throw new IllegalArgumentException("Type de modalité non pris en charge");
        }
    }

    private LocalDate calculerProchaineDateEcheance(int echeanceIndex, TypeModalite typeModalite, LocalDate dateFinEcheance) {
        switch (typeModalite) {
            case TROIS_TRANCHES:
                return obtenirDateTroisTranches(echeanceIndex, dateFinEcheance);
            case SEPT_TRANCHES:
                return obtenirDateSeptTranches(echeanceIndex, dateFinEcheance);
            case TOTALITE:
                return obtenirDateTotaliteTranches(echeanceIndex, dateFinEcheance);
            default:
                throw new IllegalArgumentException("Type de modalité non pris en charge");
        }
    }

    private LocalDate obtenirDateTroisTranches(int echeanceIndex, LocalDate dateFinEcheance) {
        LocalDate[] dates = {dateFinEcheance.withDayOfMonth(14).plusMonths(0), dateFinEcheance.withDayOfMonth(14).plusMonths(2), dateFinEcheance.withDayOfMonth(14).plusMonths(5)};
        return dates[echeanceIndex];
    }

    private LocalDate obtenirDateTotaliteTranches(int echeanceIndex, LocalDate dateFinEcheance) {
        LocalDate[] dates = {dateFinEcheance.withDayOfMonth(14).plusMonths(0),};
        return dates[echeanceIndex];
    }

    private LocalDate obtenirDateSeptTranches(int echeanceIndex, LocalDate dateDebut) {
        LocalDate[] dates = new LocalDate[7];
        for (int i = 0; i < 7; i++) {
            dates[i] = dateDebut.withDayOfMonth(14).plusMonths(i);
        }
        return dates[echeanceIndex];
    }

    public List<PaiementDto> getAllPaiements() {
        List<Paiement> paiements = paiementRepository.findAll();

        return paiements.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private PaiementDto convertToDto(Paiement paiement) {
        PaiementDto paiementDto = new PaiementDto();
        paiementDto.setEtudiantId(paiement.getEtudiant().getEtudiantId());
        paiementDto.setEtudiantNom(paiement.getEtudiant().getNom());
        paiementDto.setEtudiantPrenom(paiement.getEtudiant().getPrenom());
        paiementDto.setEtudiantMatricule(paiement.getEtudiant().getMatricule());
        paiementDto.setMontantActuel(paiement.getMontantActuel());
        paiementDto.setDatePaiement(paiement.getDatePaiement());
        paiementDto.setMontantDejaPaye(paiement.getMontantDejaPaye());
        paiementDto.setResteEcolage(paiement.getResteEcolage());
        // Ajoutez d'autres champs que vous souhaitez inclure dans le DTO
        return paiementDto;
    }

    public Paiement getLastPaiementForEtudiant(Etudiant etudiant) {
        List<Paiement> paiements = paiementRepository.findLastPaiement(etudiant);
        if (paiements.isEmpty()) {
            throw new RuntimeException("Aucun paiement trouvé pour cet étudiant.");
        }
        return paiements.get(0); // Retourne le dernier paiement
    }

    /*@Transactional
    public PaiementDto mettreAJourPaiement(PaiementDto paiementDto) {
        // Vérifier que le nouveau montant n'est pas négatif
        long nouveauMontant = paiementDto.getMontantActuel();
        if (nouveauMontant < 0) {
            throw new RuntimeException("Le nouveau montant ne peut pas être négatif.");
        }

        // Récupérer l'étudiant
        List<Etudiant> etudiants = etudiantRepository.findByNomAndPrenom(paiementDto.getEtudiantNom(), paiementDto.getEtudiantPrenom());
        if (etudiants.isEmpty()) {
            throw new RuntimeException("Aucun étudiant trouvé avec les nom et prénom spécifiés.");
        }
        Etudiant etudiant = etudiants.get(0);

        // Récupérer le dernier paiement de l'étudiant pour trouver l'ancien montant
        List<Paiement> paiements = paiementRepository.findByEtudiantOrderByDatePaiementDesc(etudiant);
        if (paiements.isEmpty()) {
            throw new RuntimeException("Aucun paiement trouvé pour cet étudiant.");
        }
        Paiement dernierPaiement = paiements.get(0);
        long ancienMontant = dernierPaiement.getMontantActuel();

        // Vérifier si l'ancien montant correspond à celui indiqué dans la requête
        if (paiementDto.getAncienMontant() != ancienMontant) {
            throw new RuntimeException("L'ancien montant ne correspond pas à celui de la dernière transaction.");
        }

        // Calculer le montant déjà payé avant ce paiement
        long montantDejaPaye = paiements.stream().mapToLong(Paiement::getMontantActuel).sum() - ancienMontant;

        // Ajouter le nouveau montant payé à la place de l'ancien montant
        montantDejaPaye += nouveauMontant;

        // Calculer le montant final des frais de scolarité
        Reduction reduction = etudiant.getReduction();
        long montantFinal = (reduction != null) ? reduction.getMontantFinal() : 0;

        // Calculer le reste à payer
        long resteEcolage = montantFinal - montantDejaPaye;

        // S'assurer que resteEcolage ne devient pas négatif
        resteEcolage = Math.max(resteEcolage, 0);

        // Mettre à jour le dernier paiement avec le nouveau montant
        dernierPaiement.setMontantActuel(nouveauMontant);
        dernierPaiement.setDatePaiement(paiementDto.getDatePaiement());
        dernierPaiement.setMontantDejaPaye(montantDejaPaye);
        dernierPaiement.setResteEcolage(resteEcolage);

        // Sauvegarder le paiement mis à jour
        paiementRepository.save(dernierPaiement);

        // Déterminer le statut de scolarité mis à jour
        dernierPaiement.setStatutScolarite(resteEcolage == 0 ? StatutScolarite.SOLDE : StatutScolarite.EN_COURS);

        // Récupérer le tuteur et envoyer un email de confirmation si nécessaire
        Tuteur tuteur = etudiant.getTuteur();
        if (tuteur != null && tuteur.getEmail() != null && !tuteur.getEmail().isEmpty()) {
            String tuteurMail = tuteur.getEmail();
            String subject = "Mise à jour de Paiement pour " + etudiant.getNom() + " " + etudiant.getPrenom();

            // Contenu de l'email à envoyer
            String text = "Bonjour,\n\n" +
                    "Nous vous informons que le montant de " + ancienMontant + " a été mis à jour avec le nouveau montant de " +
                    nouveauMontant + " pour l'étudiant " + etudiant.getNom() + " " + etudiant.getPrenom() + " le " + paiementDto.getDatePaiement() + ".\n" +
                    "Montant déjà payé : " + montantDejaPaye + "\n" +
                    "Reste à payer : " + resteEcolage + "\n\n" +
                    "Cordialement,\nL'administration.";

            // Envoi de l'email
            emailService.sendEmail(tuteurMail, subject, text);
        } else {
            System.out.println("Erreur : Le tuteur ou son adresse email n'est pas défini(e).");
        }

        String tuteurMail = (tuteur != null) ? tuteur.getEmail() : null;

        // Préparer la réponse mise  jour
        PaiementDto paiementResponse = new PaiementDto();
        paiementResponse.setEtudiantId(etudiant.getEtudiantId());
        paiementResponse.setEtudiantNom(etudiant.getNom());
        paiementResponse.setEtudiantPrenom(etudiant.getPrenom());
        paiementResponse.setEtudiantMatricule(etudiant.getMatricule());
        paiementResponse.setFiliereNom(etudiant.getFiliere().getNomFiliere());
        paiementResponse.setParcoursNom(etudiant.getParcours().getNomParcours());
        paiementResponse.setReductionMontantFinal(montantFinal);
        paiementResponse.setNiveauEtude(etudiant.getNiveauEtude());
        paiementResponse.setMontantDejaPaye(montantDejaPaye);
        paiementResponse.setDatePaiement(paiementDto.getDatePaiement());
        paiementResponse.setMontantActuel(nouveauMontant);
        paiementResponse.setResteEcolage(resteEcolage);
        paiementResponse.setTuteurMail(tuteurMail);
        paiementResponse.setStatutScolarite(resteEcolage == 0 ? StatutScolarite.SOLDE : StatutScolarite.EN_COURS);

        return paiementResponse;
    }*/



    private List<EcheanceDto> calculerEcheances(Etudiant etudiant, long montantFinal, long montantDejaPaye) {
        List<EcheanceDto> echeanceDTOs = new ArrayList<>();

        // Étape 1 : Récupérer la modalité de l'étudiant
        TypeModalite modalite = etudiant.getTypeModalite();

        // Étape 2 : Déterminer le nombre d'échéances
        Integer nombreEcheances = obtenirNombreEcheancesPourModalite(modalite);

        // Étape 3 : Calculer le montant par échéance
        long montantParEcheance = montantFinal / nombreEcheances;
        long montantRestant = montantFinal % nombreEcheances; // Pour ajuster le premier paiement

        // Date de début pour les échéances
        LocalDate dateFinEcheance = LocalDate.now().withMonth(11).withDayOfMonth(14); // Date de début pour les échéances

        // Étape 4 : Créer les échéances
        for (int i = 0; i < nombreEcheances; i++) {
            EcheanceDto echeanceDto = new EcheanceDto();
            echeanceDto.setId(Long.valueOf(i + 1));

            // Ajuster le montant pour la première échéance
            long montantPourCetteEcheance = (i == 0) ? montantParEcheance + montantRestant : montantParEcheance;

            echeanceDto.setMontantParEcheance(montantPourCetteEcheance);
            echeanceDto.setDateEcheance(calculerProchaineDateEcheance(i, modalite, dateFinEcheance));

            // Vérification de l'état de chaque échéance
            if (montantDejaPaye >= montantPourCetteEcheance) {
                // Cas où l'échéance est totalement payée
                echeanceDto.setStatut(StatutEcheance.PAYEE);
                montantDejaPaye -= montantPourCetteEcheance;
            } else if (montantDejaPaye > 0) {
                // Cas où l'échéance est partiellement payée
                echeanceDto.setStatut(StatutEcheance.PARTIELLEMENT_PAYEE);
                montantDejaPaye = 0; // Le reste du montant est maintenant épuisé
            } else {
                // Cas où l'échéance est en attente de paiement
                echeanceDto.setStatut(StatutEcheance.EN_ATTENTE);
            }

            echeanceDTOs.add(echeanceDto);
        }

        return echeanceDTOs;
    }


    public List<DtoFiliere> getAllFiliereDtos() {
        // Récupérer toutes les filières depuis la base de données
        List<Filiere> filieres = filiereRepository.findAll();

        // Mapper les objets Filiere vers des DtoFiliere
        return filieres.stream().map(this::mapToDtoFiliere).collect(Collectors.toList());
    }

    private DtoFiliere mapToDtoFiliere(Filiere filiere) {
        DtoFiliere dtoFiliere = new DtoFiliere();
        dtoFiliere.setNomFiliere(filiere.getNomFiliere()); // Mapper uniquement le nom de la filière
        return dtoFiliere;
    }


    public List<PaiementDto> getDernierPaiementByFiliereAndNiveau(String nomFiliere, String niveauEtude) {
        // Rechercher la filière par nom
        Filiere filiere = filiereRepository.findByNomFiliere(nomFiliere).orElseThrow(() -> new RuntimeException("Filière non trouvée"));

        // Valider le niveau d'étude
        NiveauEtude niveau;
        try {
            niveau = NiveauEtude.valueOf(niveauEtude.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Niveau d'étude invalide");
        }

        // Récupérer les étudiants par filière et niveau d'étude
        List<Etudiant> etudiants = etudiantRepository.findByFiliereAndNiveauEtude(filiere, niveau);

        // Récupérer tous les paiements pour les étudiants
        List<Paiement> paiements = paiementRepository.findByEtudiantIn(etudiants);

        // Map pour stocker le dernier paiement de chaque étudiant
        Map<Etudiant, Paiement> dernierPaiementMap = new HashMap<>();

        for (Paiement paiement : paiements) {
            Etudiant etudiant = paiement.getEtudiant();
            // Si l'étudiant n'est pas encore dans la map ou si le paiement est plus récent
            dernierPaiementMap.merge(etudiant, paiement, (ancien, nouveau) -> ancien.getDatePaiement().isAfter(nouveau.getDatePaiement()) ? ancien : nouveau);
        }

        // Convertir la map en liste de DTO
        List<PaiementDto> paiementDtos = new ArrayList<>();
        for (Map.Entry<Etudiant, Paiement> entry : dernierPaiementMap.entrySet()) {
            Etudiant etudiant = entry.getKey();
            Paiement dernierPaiement = entry.getValue();

            PaiementDto dto = new PaiementDto();
            dto.setEtudiantId(etudiant.getEtudiantId());
            dto.setEtudiantNom(etudiant.getNom());
            dto.setEtudiantPrenom(etudiant.getPrenom());
            dto.setEtudiantMatricule(etudiant.getMatricule());
            dto.setFiliereNom(etudiant.getFiliere().getNomFiliere());
            dto.setParcoursNom(etudiant.getParcours().getNomParcours());
            dto.setReductionMontantFinal(etudiant.getReduction().getMontantFinal());
            dto.setNiveauEtude(etudiant.getNiveauEtude());
            dto.setMontantDejaPaye(dernierPaiement.getMontantDejaPaye());
            dto.setTypeModalite(etudiant.getTypeModalite());
            dto.setDatePaiement(dernierPaiement.getDatePaiement());
            dto.setMontantActuel(dernierPaiement.getMontantActuel());
            dto.setResteEcolage(dernierPaiement.getResteEcolage());

            // Ajouter le calcul des échéances basé sur la modalité
            List<EcheanceDto> echeances = calculerEcheancesPourPaiement(etudiant, dernierPaiement);
            dto.setEcheances(echeances);

            paiementDtos.add(dto);
        }

        // Trier les DTOs par ID d'étudiant en ordre croissant
        return paiementDtos.stream().sorted(Comparator.comparing(PaiementDto::getEtudiantId)).collect(Collectors.toList());
    }


    private List<EcheanceDto> calculerEcheancesPourPaiement(Etudiant etudiant, Paiement paiement) {
        TypeModalite modalite = etudiant.getTypeModalite();
        Integer nombreEcheances = obtenirNombreEcheancesPourModalite(modalite);

        // Date de début ajustée au 14 octobre de l'année en cours
        LocalDate dateDebut = LocalDate.now().withMonth(11).withDayOfMonth(14);

        long montantFinal = etudiant.getReduction().getMontantFinal();
        long montantDejaPaye = paiement.getMontantDejaPaye();

        // Diviser le montant final par le nombre d'échéances, en arrondissant à l'entier inférieur
        long montantParEcheance = montantFinal / nombreEcheances;

        // Calculer le montant restant à répartir
        long montantRestant = montantFinal % nombreEcheances;

        List<EcheanceDto> echeanceDTOs = new ArrayList<>();

        for (int i = 0; i < nombreEcheances; i++) {
            EcheanceDto echeanceDto = new EcheanceDto();
            echeanceDto.setId(Long.valueOf(i + 1));

            // Ajuster la première ou les premières échéances pour inclure le montant restant
            long montantPourCetteEcheance = (i == 0) ? montantParEcheance + montantRestant : montantParEcheance;

            echeanceDto.setMontantParEcheance(montantPourCetteEcheance);
            echeanceDto.setDateEcheance(calculerProchaineDateEcheance(i, modalite, dateDebut));

            // Vérification de l'état de chaque échéance
            if (montantDejaPaye >= montantPourCetteEcheance) {
                echeanceDto.setStatut(StatutEcheance.PAYEE);
                montantDejaPaye -= montantPourCetteEcheance;
            } else if (montantDejaPaye > 0 && montantDejaPaye < montantPourCetteEcheance) {
                echeanceDto.setStatut(StatutEcheance.PARTIELLEMENT_PAYEE);
                montantDejaPaye = 0;
            } else {
                echeanceDto.setStatut(StatutEcheance.EN_ATTENTE);
            }

            echeanceDTOs.add(echeanceDto);
        }

        return echeanceDTOs;
    }


    public List<PaiementDto> getPaiementsEnCours() {
        List<Paiement> paiementsEnCours = paiementRepository.findByStatutScolarite(StatutScolarite.EN_COURS);
        List<PaiementDto> paiementDtos = new ArrayList<>();
        for (Paiement paiement : paiementsEnCours) {
            // Vérifier s'il existe un paiement pour cet étudiant avec le statut SOLDE
            boolean hasSolde = paiementRepository.existsByEtudiantAndStatutScolarite(paiement.getEtudiant(), StatutScolarite.SOLDE);

            // Ignorer cet étudiant s'il a un statut SOLDE
            if (hasSolde) {
                continue;
            }

            // Créer le DTO et ajouter à la liste seulement si resteEcolage > 0
            PaiementDto paiementDto = new PaiementDto();
            paiementDto.setEtudiantNom(paiement.getEtudiant().getNom());
            paiementDto.setEtudiantPrenom(paiement.getEtudiant().getPrenom());
            paiementDto.setFiliereNom(paiement.getEtudiant().getFiliere().getNomFiliere());
            paiementDto.setParcoursNom(paiement.getEtudiant().getParcours().getNomParcours());
            paiementDto.setReductionMontantFinal(paiement.getEtudiant().getReduction().getMontantFinal());
            paiementDto.setNiveauEtude(paiement.getEtudiant().getNiveauEtude());
            paiementDto.setMontantDejaPaye(paiement.getMontantDejaPaye());
            paiementDto.setResteEcolage(paiement.getResteEcolage());

            // Ajouter à la liste seulement si resteEcolage > 0
            if (paiementDto.getResteEcolage() > 0) {
                paiementDtos.add(paiementDto);
            }
        }
        return paiementDtos;
    }




    ///////////////////////////////////////////////////////////////////
    /////////////////////RAPPELS////////////////////////


    public List<PaiementDto> getPaiementsPourTousLesEtudiants() {
        // Récupérer tous les étudiants
        List<Etudiant> etudiants = etudiantRepository.findAll();

        // Créer une liste pour stocker les résultats
        List<PaiementDto> paiementsDto = new ArrayList<>();

        // Boucler sur chaque étudiant
        for (Etudiant etudiant : etudiants) {
            // Récupérer tous les paiements associés à cet étudiant
            List<Paiement> paiements = paiementRepository.findByEtudiant(etudiant);

            if (paiements.isEmpty()) {
                continue; // Si aucun paiement n'est trouvé, on passe au suivant
            }

            // Utiliser le dernier paiement effectué pour les informations principales
            Paiement dernierPaiement = paiements.get(paiements.size() - 1);

            // Calculer le montant déjà payé par l'étudiant
            long montantDejaPaye = paiements.stream().mapToLong(Paiement::getMontantActuel).sum();

            // Récupérer la réduction et calculer le montant final
            Reduction reduction = etudiant.getReduction();
            long montantFinal = (reduction != null) ? reduction.getMontantFinal() : 0;

            // Calculer le reste à payer
            long resteEcolage = montantFinal - montantDejaPaye;
            resteEcolage = Math.max(resteEcolage, 0);  // Pour éviter les montants négatifs

            // Calculer le nombre d'échéances
            TypeModalite modalite = etudiant.getTypeModalite();
            Integer nombreEcheances = obtenirNombreEcheancesPourModalite(modalite);

            // Date de début ajustée au 14 NOVEMBRE de l'année en cours
            LocalDate dateFinEcheance = LocalDate.now().withMonth(11).withDayOfMonth(14);

            // Créer les échéances pour cet étudiant (comme dans le code précédent)

            List<EcheanceDto> echeanceDTOs = new ArrayList<>();
            long montantRestant = montantDejaPaye;

            for (int i = 0; i < nombreEcheances; i++) {
                EcheanceDto echeanceDto = new EcheanceDto();
                echeanceDto.setId((long) (i + 1));
                long montantPourCetteEcheance = (i == 0) ? montantFinal / nombreEcheances : montantFinal / nombreEcheances;
                echeanceDto.setMontantParEcheance(montantPourCetteEcheance);
                echeanceDto.setDateEcheance(calculerProchaineDateEcheance(i, modalite, dateFinEcheance));
                echeanceDto.setDateEnvoi(echeanceDto.getDateEcheance().minusWeeks(2));

                // Calcul du reste sur chaque échéance
                echeanceDto.setResteSurEcheance(Math.max(montantPourCetteEcheance - montantRestant, 0));

                // Vérifier l'état de chaque échéance
                if (montantRestant >= montantPourCetteEcheance) {
                    echeanceDto.setStatut(StatutEcheance.PAYEE);
                    montantRestant -= montantPourCetteEcheance;
                } else if (montantRestant > 0) {
                    echeanceDto.setStatut(StatutEcheance.PARTIELLEMENT_PAYEE);
                    montantRestant = 0;
                } else {
                    echeanceDto.setStatut(StatutEcheance.EN_ATTENTE);
                }

                echeanceDTOs.add(echeanceDto);
            }


            // Récupérer le tuteur et son email
            Tuteur tuteur = etudiant.getTuteur();
            String tuteurMail = (tuteur != null) ? tuteur.getEmail() : null;

            // Créer l'objet PaiementDto pour cet étudiant
            PaiementDto paiementResponse = new PaiementDto();
            paiementResponse.setEtudiantId(etudiant.getEtudiantId());
            paiementResponse.setEtudiantNom(etudiant.getNom());
            paiementResponse.setEtudiantPrenom(etudiant.getPrenom());
            paiementResponse.setEtudiantMatricule(etudiant.getMatricule());
            paiementResponse.setFiliereNom(etudiant.getFiliere().getNomFiliere());
            paiementResponse.setParcoursNom(etudiant.getParcours().getNomParcours());
            paiementResponse.setReductionMontantFinal(montantFinal);
            paiementResponse.setNiveauEtude(etudiant.getNiveauEtude());
            paiementResponse.setMontantDejaPaye(montantDejaPaye);
            paiementResponse.setTypeModalite(TypeModalite.valueOf(modalite.name()));
            paiementResponse.setDatePaiement(dernierPaiement.getDatePaiement());
            paiementResponse.setMontantActuel(dernierPaiement.getMontantActuel());
            paiementResponse.setResteEcolage(resteEcolage);
            paiementResponse.setEcheances(echeanceDTOs);
            paiementResponse.setTuteurMail(tuteurMail);
            paiementResponse.setStatutScolarite((resteEcolage == 0) ? StatutScolarite.SOLDE : StatutScolarite.EN_COURS);

            // Ajouter à la liste
            paiementsDto.add(paiementResponse);
        }

        // Retourner la liste des paiements pour tous les étudiants
        return paiementsDto;
    }



    public List<PaiementDto> getPaiementsAvecEcheanceNonReglee() {
        // Récupérer tous les étudiants
        List<Etudiant> etudiants = etudiantRepository.findAll();

        // Créer une liste pour stocker les résultats
        List<PaiementDto> paiementsNonRegles = new ArrayList<>();

        // Boucler sur chaque étudiant
        for (Etudiant etudiant : etudiants) {
            // Récupérer tous les paiements associés à cet étudiant
            List<Paiement> paiements = paiementRepository.findByEtudiant(etudiant);

            if (paiements.isEmpty()) {
                continue; // Si aucun paiement n'est trouvé, on passe au suivant
            }

            // Utiliser le dernier paiement effectué pour les informations principales
            Paiement dernierPaiement = paiements.get(paiements.size() - 1);

            // Calculer le montant déjà payé
            long montantDejaPaye = paiements.stream().mapToLong(Paiement::getMontantActuel).sum();

            // Récupérer la réduction et calculer le montant final
            Reduction reduction = etudiant.getReduction();
            long montantFinal = (reduction != null) ? reduction.getMontantFinal() : 0;

            // Calculer le reste à payer
            long resteEcolage = montantFinal - montantDejaPaye;
            resteEcolage = Math.max(resteEcolage, 0);  // Pour éviter les montants négatifs

            // Calculer le nombre d'échéances
            TypeModalite modalite = etudiant.getTypeModalite();
            Integer nombreEcheances = obtenirNombreEcheancesPourModalite(modalite);

            // Date de début ajustée au 14 NOVEMBRE de l'année en cours
            LocalDate dateFinEcheance = LocalDate.now().withMonth(11).withDayOfMonth(14);

            // Créer les échéances
            List<EcheanceDto> echeanceDTOs = new ArrayList<>();
            long montantRestant = montantDejaPaye;

            if (nombreEcheances == 1) {
                EcheanceDto echeanceDto = new EcheanceDto();
                echeanceDto.setId(1L);
                echeanceDto.setMontantParEcheance(montantFinal);
                echeanceDto.setDateEcheance(dateFinEcheance);
                echeanceDto.setDateEnvoi(dateFinEcheance.minusWeeks(2));
                echeanceDto.setResteSurEcheance(montantFinal - montantRestant);

                // Déterminer le statut de l'échéance
                if (montantDejaPaye >= montantFinal) {
                    echeanceDto.setStatut(StatutEcheance.PAYEE);
                } else if (montantDejaPaye > 0) {
                    echeanceDto.setStatut(StatutEcheance.PARTIELLEMENT_PAYEE);
                } else {
                    echeanceDto.setStatut(StatutEcheance.EN_ATTENTE);
                }

                echeanceDTOs.add(echeanceDto);
            } else {
                long montantSuppl = 10000;
                long montantParEcheance = (montantFinal - montantSuppl) / (nombreEcheances - 1);

                for (int i = 0; i < nombreEcheances; i++) {
                    EcheanceDto echeanceDto = new EcheanceDto();
                    echeanceDto.setId(Long.valueOf(i + 1));

                    long montantPourCetteEcheance = (i == 0) ? montantParEcheance + montantSuppl : montantParEcheance;
                    echeanceDto.setMontantParEcheance(montantPourCetteEcheance);
                    echeanceDto.setDateEcheance(calculerProchaineDateEcheance(i, modalite, dateFinEcheance));
                    echeanceDto.setDateEnvoi(echeanceDto.getDateEcheance().minusWeeks(2));

                    echeanceDto.setResteSurEcheance(Math.max(montantPourCetteEcheance - montantRestant, 0));

                    // Vérification de l'état de l'échéance
                    if (montantRestant >= montantPourCetteEcheance) {
                        echeanceDto.setStatut(StatutEcheance.PAYEE);
                        montantRestant -= montantPourCetteEcheance;
                    } else if (montantRestant > 0) {
                        echeanceDto.setStatut(StatutEcheance.PARTIELLEMENT_PAYEE);
                        montantRestant = 0;
                    } else {
                        echeanceDto.setStatut(StatutEcheance.EN_ATTENTE);
                    }

                    echeanceDTOs.add(echeanceDto);
                }
            }


            // Trouver l'échéance avec l'ID 1
            EcheanceDto echeance1 = echeanceDTOs.stream()
                    .filter(echeance -> echeance.getId() == 1)
                    .findFirst()
                    .orElse(null);

            // Vérifier si l'échéance ID 1 n'est pas encore payée
            if (echeance1 != null && !echeance1.getStatut().equals(StatutEcheance.PAYEE)) {
                // Créer l'objet PaiementDto pour cet étudiant
                PaiementDto paiementResponse = new PaiementDto();
                paiementResponse.setEtudiantId(etudiant.getEtudiantId());
                paiementResponse.setEtudiantNom(etudiant.getNom());
                paiementResponse.setEtudiantPrenom(etudiant.getPrenom());
                paiementResponse.setEtudiantMatricule(etudiant.getMatricule());
                paiementResponse.setFiliereNom(etudiant.getFiliere().getNomFiliere());
                paiementResponse.setParcoursNom(etudiant.getParcours().getNomParcours());
                paiementResponse.setReductionMontantFinal(montantFinal);
                paiementResponse.setNiveauEtude(etudiant.getNiveauEtude());
                paiementResponse.setMontantDejaPaye(montantDejaPaye);
                paiementResponse.setTypeModalite(TypeModalite.valueOf(modalite.name()));
                paiementResponse.setDatePaiement(dernierPaiement.getDatePaiement());
                paiementResponse.setMontantActuel(dernierPaiement.getMontantActuel());
                paiementResponse.setResteEcolage(resteEcolage);

                // Créer une liste pour ne garder que l'échéance ID 1
                List<EcheanceDto> echeanceFiltrée = new ArrayList<>();
                echeanceFiltrée.add(echeance1);  // Ajouter uniquement l'échéance ID 1

                // Ajouter uniquement l'échéance filtrée
                paiementResponse.setEcheances(echeanceFiltrée);

                paiementResponse.setTuteurMail(etudiant.getTuteur() != null ? etudiant.getTuteur().getEmail() : null);
                paiementResponse.setStatutScolarite((resteEcolage == 0) ? StatutScolarite.SOLDE : StatutScolarite.EN_COURS);

                // Ajouter à la liste si l'échéance n'est pas encore réglée
                paiementsNonRegles.add(paiementResponse);
            }

        }

        // Retourner la liste des paiements avec échéance ID 1 non réglée
        return paiementsNonRegles;
    }

    public long compterPaiementsAvecEcheanceNonReglee() {
        // Appeler la méthode existante pour obtenir les paiements non réglés
        List<PaiementDto> paiementsNonRegles = getPaiementsAvecEcheanceNonReglee();

        // Retourner le nombre de paiements non réglés
        return paiementsNonRegles.size();
    }


////////////////////////////LISTE DE RENVOI /////////////////////////////////////////////////
    public List<PaiementDto> getListeRenvoiTous() {
        // Récupérer tous les étudiants
        List<Etudiant> etudiants = etudiantRepository.findAll();

        // Obtenir le mois en cours
        int moisEnCours = LocalDate.now().getMonthValue();

        // Créer une liste pour stocker les résultats
        List<PaiementDto> paiementsDto = new ArrayList<>();

        // Boucler sur chaque étudiant
        for (Etudiant etudiant : etudiants) {
            // Récupérer tous les paiements associés à cet étudiant
            List<Paiement> paiements = paiementRepository.findByEtudiant(etudiant);

            if (paiements.isEmpty()) {
                continue; // Si aucun paiement n'est trouvé, on passe au suivant
            }

            // Utiliser le dernier paiement effectué pour les informations principales
            Paiement dernierPaiement = paiements.get(paiements.size() - 1);

            // Calculer le montant déjà payé par l'étudiant
            long montantDejaPaye = paiements.stream().mapToLong(Paiement::getMontantActuel).sum();

            // Récupérer la réduction et calculer le montant final
            Reduction reduction = etudiant.getReduction();
            long montantFinal = (reduction != null) ? reduction.getMontantFinal() : 0;

            // Calculer le reste à payer
            long resteEcolage = montantFinal - montantDejaPaye;
            resteEcolage = Math.max(resteEcolage, 0);  // Pour éviter les montants négatifs

            // Calculer le nombre d'échéances
            TypeModalite modalite = etudiant.getTypeModalite();
            Integer nombreEcheances = obtenirNombreEcheancesPourModalite(modalite);

            // Date de début ajustée au 14 NOVEMBRE de l'année en cours
            LocalDate dateFinEcheance = LocalDate.now().withMonth(11).withDayOfMonth(14);

            // Créer les échéances pour cet étudiant
            List<EcheanceDto> echeanceDTOs = new ArrayList<>();
            long montantRestant = montantDejaPaye;

            for (int i = 0; i < nombreEcheances; i++) {
                EcheanceDto echeanceDto = new EcheanceDto();
                echeanceDto.setId((long) (i + 1));
                long montantPourCetteEcheance = montantFinal / nombreEcheances;
                echeanceDto.setMontantParEcheance(montantPourCetteEcheance);
                echeanceDto.setDateEcheance(calculerProchaineDateEcheance(i, modalite, dateFinEcheance));
                echeanceDto.setDateEnvoi(echeanceDto.getDateEcheance().minusWeeks(2));

                // Calcul du reste sur chaque échéance
                echeanceDto.setResteSurEcheance(Math.max(montantPourCetteEcheance - montantRestant, 0));

                // Vérifier l'état de chaque échéance
                if (montantRestant >= montantPourCetteEcheance) {
                    echeanceDto.setStatut(StatutEcheance.PAYEE);
                    montantRestant -= montantPourCetteEcheance;
                } else if (montantRestant > 0) {
                    echeanceDto.setStatut(StatutEcheance.PARTIELLEMENT_PAYEE);
                    montantRestant = 0;
                } else {
                    echeanceDto.setStatut(StatutEcheance.EN_ATTENTE);
                }

                // Ajouter chaque échéance à la liste
                echeanceDTOs.add(echeanceDto);
            }

            // Filtrer les étudiants dont la `dateEnvoi` est dans le mois en cours et dont le statut d’échéance est `EN_ATTENTE` ou `PARTIELLEMENT_PAYEE`
            boolean echeanceValide = echeanceDTOs.stream()
                    .anyMatch(echeance -> echeance.getDateEnvoi().getMonthValue() == moisEnCours
                            && (echeance.getStatut() == StatutEcheance.EN_ATTENTE
                            || echeance.getStatut() == StatutEcheance.PARTIELLEMENT_PAYEE));

            if (!echeanceValide) {
                continue; // Si aucune échéance valide n'est trouvée, on passe à l'étudiant suivant
            }

            // Récupérer le tuteur et son email
            Tuteur tuteur = etudiant.getTuteur();
            String tuteurMail = (tuteur != null) ? tuteur.getEmail() : null;

            // Créer l'objet PaiementDto pour cet étudiant
            PaiementDto paiementResponse = new PaiementDto();
            paiementResponse.setEtudiantId(etudiant.getEtudiantId());
            paiementResponse.setEtudiantNom(etudiant.getNom());
            paiementResponse.setEtudiantPrenom(etudiant.getPrenom());
            paiementResponse.setEtudiantMatricule(etudiant.getMatricule());
            paiementResponse.setFiliereNom(etudiant.getFiliere().getNomFiliere());
            paiementResponse.setParcoursNom(etudiant.getParcours().getNomParcours());
            paiementResponse.setReductionMontantFinal(montantFinal);
            paiementResponse.setNiveauEtude(etudiant.getNiveauEtude());
            paiementResponse.setMontantDejaPaye(montantDejaPaye);
            paiementResponse.setTypeModalite(TypeModalite.valueOf(modalite.name()));
            paiementResponse.setDatePaiement(dernierPaiement.getDatePaiement());
            paiementResponse.setMontantActuel(dernierPaiement.getMontantActuel());
            paiementResponse.setResteEcolage(resteEcolage);
            paiementResponse.setEcheances(echeanceDTOs);
            paiementResponse.setTuteurMail(tuteurMail);
            paiementResponse.setStatutScolarite((resteEcolage == 0) ? StatutScolarite.SOLDE : StatutScolarite.EN_COURS);

            // Ajouter à la liste
            paiementsDto.add(paiementResponse);
        }

        // Retourner la liste des paiements pour les étudiants éligibles
        return paiementsDto;
    }


}








