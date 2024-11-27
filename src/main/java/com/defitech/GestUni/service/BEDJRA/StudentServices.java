package com.defitech.GestUni.service.BEDJRA;

import com.defitech.GestUni.dto.BEDJRA.PaiementDto;
import com.defitech.GestUni.dto.BEDJRA.ParcoursStatsDTO;
import com.defitech.GestUni.dto.BEDJRA.StudentDto;
import com.defitech.GestUni.enums.BEDJRA.MentionBac;
import com.defitech.GestUni.enums.BEDJRA.StatutEcheance;
import com.defitech.GestUni.enums.BEDJRA.StatutScolarite;
import com.defitech.GestUni.enums.BEDJRA.Statutboursier;
import com.defitech.GestUni.enums.NiveauEtude;
import com.defitech.GestUni.models.BEDJRA.Paiement;
import com.defitech.GestUni.models.BEDJRA.Reduction;
import com.defitech.GestUni.models.Bases.Etudiant;
import com.defitech.GestUni.models.Bases.Filiere;
import com.defitech.GestUni.models.Bases.Parcours;
import com.defitech.GestUni.models.Bases.Tuteur;
import com.defitech.GestUni.repository.BEDJRA.*;
import com.defitech.GestUni.repository.EtudiantRepository;
import com.defitech.GestUni.repository.FiliereRepository;
import com.defitech.GestUni.repository.ParcoursRepository;
import com.defitech.GestUni.service.FiliereServices;
import com.defitech.GestUni.service.ParcoursServices;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class StudentServices {
    @Autowired
    private EtudiantRepository etudiantRepository;
    @Autowired
    private TuteurRepository tuteurRepository;
    @Autowired
    private ParcoursRepository parcoursRepository;
    @Autowired
    private FiliereRepository filiereRepository;
    @Autowired
    private ReductionRepository reductionRepository;
    @Autowired
    private PaiementRepository paiementRepository;
    @Autowired
    private ParcoursServices parcoursServices;
    @Autowired
    private FiliereServices filiereServices;
    @Autowired
    private EcheanceRepository echeanceRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private PaiementService paiementService;

    ///////////////////// Endpoint pour ajouter un étudiant//////////////////////////////
    public Long calculerMontantScolarite(Statutboursier statutboursier, String nomparcours, MentionBac mentionBac) {
        Long montant = 0L;

        switch (statutboursier) {
            case DOUBLANT_BTS:
                // Montants spécifiques pour le statut Compassion
                switch (nomparcours) {
                    case "BTS":
                        montant = 250000L; // Montant pour compassion en BTS
                        break;
                    default:
                        throw new IllegalArgumentException("Parcours inconnu pour un étudiant en compassion !");
                }
                break;
            case COMPASSION:
                // Montants spécifiques pour le statut Compassion
                switch (nomparcours) {
                    case "BTS":
                        montant = 225000L; // Montant pour compassion en BTS
                        break;
                    case "Licence du jour":
                        montant = 335000L; // Montant pour compassion en Licence du jour
                        break;
                    case "Licence du soir":
                        montant = 335000L; // Montant pour compassion en Licence du soir
                        break;
                    default:
                        throw new IllegalArgumentException("Parcours inconnu pour un étudiant en compassion !");
                }
                break;

            case OUI:
                // Montants spécifiques pour les boursiers
                switch (nomparcours) {
                    case "BTS":
                        switch (mentionBac) {
                            case ASSEZ_BIEN:
                            case BIEN:
                            case TRES_BIEN:
                                montant = 360000L;
                                break;
                            case PASSABLE:
                                montant = 380000L;
                                break;
                            default:
                                throw new IllegalArgumentException("Mention inconnue pour un étudiant boursier !");
                        }
                        break;
                    case "Licence du jour":
                        switch (mentionBac) {
                            case ASSEZ_BIEN:
                            case BIEN:
                            case TRES_BIEN:
                                montant = 470000L;
                                break;
                            case PASSABLE:
                                montant = 490000L;
                                break;
                            default:
                                throw new IllegalArgumentException("Mention inconnue pour un étudiant boursier !");
                        }
                        break;
                    case "Licence du soir":
                        switch (mentionBac) {
                            case ASSEZ_BIEN:
                            case BIEN:
                            case TRES_BIEN:
                                montant = 470000L;
                                break;
                            case PASSABLE:
                                montant = 490000L;
                                break;
                            default:
                                throw new IllegalArgumentException("Mention inconnue pour un étudiant boursier !");
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Parcours inconnu pour un étudiant boursier !");
                }
                break;

            case NON:
                // Montants spécifiques pour les non-boursiers
                switch (nomparcours) {
                    case "BTS":
                        montant = 410000L;
                        break;
                    case "Licence du jour":
                        montant = 520000L;
                        break;
                    case "Licence du soir":
                        montant = 520000L;
                        break;
                    default:
                        throw new IllegalArgumentException("Parcours inconnu pour un étudiant non-boursier !");
                }
                break;

            default:
                throw new IllegalArgumentException("Statut boursier inconnu !");
        }

        return montant;
    }

    @Transactional
    public Etudiant saveEtudiant(StudentDto studentDto) {
        // Création et sauvegarde du tuteur
        Tuteur tuteur = new Tuteur();
        tuteur.setNom(studentDto.getTuteurNom());
        tuteur.setPrenom(studentDto.getTuteurPrenom());
        tuteur.setProfession(studentDto.getTuteurProfession());
        tuteur.setOrganismeEmployeur(studentDto.getTuteurOrganismeEmployeur());
        tuteur.setAdresse(studentDto.getTuteurAdresse());
        tuteur.setTelBureau(studentDto.getTuteurTelBureau());
        tuteur.setTelDom(studentDto.getTuteurTelDom());
        tuteur.setCel(studentDto.getTuteurCel());
        tuteur.setEmail(studentDto.getTuteurEmail());
        tuteurRepository.save(tuteur);

        // Récupération du Parcours et de la Filière
        Parcours parcours = parcoursRepository.findById(studentDto.getParcoursId())
                .orElseThrow(() -> new RuntimeException("Parcours not found"));

        Filiere filiere = filiereRepository.findByNomFiliere(studentDto.getNomFiliere())
                .orElseThrow(() -> new RuntimeException("Filiere not found"));


        // Calcul du montant de la scolarité
        Statutboursier statutboursier = studentDto.getBoursier();  // La chaîne peut être "Oui", "Non", "Compassion"
        MentionBac mentionbac = studentDto.getMentionBac();

        long montantScolarite = calculerMontantScolarite(statutboursier, parcours.getNomParcours(), mentionbac);

        // Création de l'étudiant et assignation des entités associées
        Etudiant etudiant = new Etudiant();
        etudiant.setNom(studentDto.getEtudiantNom());
        etudiant.setPrenom(studentDto.getEtudiantPrenom());
        etudiant.setAdresse(studentDto.getEtudiantAdresse());
        etudiant.setTelephone(studentDto.getEtudiantTelephone());
        etudiant.setEmail(studentDto.getEtudiantMail());
        etudiant.setDateNaiss(studentDto.getEtudiantDateNaiss());
        etudiant.setLieuNaiss(studentDto.getEtudiantLieuNais());
        etudiant.setNationnalite(studentDto.getEtudiantNationnalite());
        etudiant.setSexe(studentDto.getEtudiantSexe());
        etudiant.setSerieBac(studentDto.getEtudiantSerieBac());
        etudiant.setAnneeBac(studentDto.getEtudiantAnneeBac());
        etudiant.setEtatProvenance(studentDto.getEtudiantEtatProvenance());
        etudiant.setDateIns(studentDto.getEtudiantDateIns());
        etudiant.setPaysBac(studentDto.getEtudiantPaysBac());
        etudiant.setAutreDiplome(studentDto.getEtudiantAutreDiplome());

        etudiant.setMentionBac(studentDto.getMentionBac());
        etudiant.setStatutboursier(studentDto.getBoursier());

        etudiant.setNiveauEtude(studentDto.getNiveauEtude());

        etudiant.setTypeModalite(studentDto.getTypeModalite());


        // Création et association de la réduction
        Reduction reduction = new Reduction();
        reduction.setMontantFinal(montantScolarite);
        reductionRepository.save(reduction);

        etudiant.setReduction(reduction);
        etudiant.setFiliere(filiere);
        etudiant.setParcours(parcours);
        etudiant.setTuteur(tuteur);

        // Sauvegarde de l'étudiant et génération du matricule
        etudiant = etudiantRepository.save(etudiant);
        String matricule = filiere.getNomFiliere().substring(0, 2).toUpperCase() + "-"
                + studentDto.getNiveauEtude().getCode() + "-"
                + etudiant.getEtudiantId() + "-"
                + etudiant.getNom().substring(0, 2).toUpperCase();
        etudiant.setMatricule(matricule);
        etudiant = etudiantRepository.save(etudiant);  // Mise à jour de l'étudiant avec le matricule géné

        // Enregistrement des informations de paiement associées à l'étudiant
        Paiement paiement = new Paiement();
        paiement.setEtudiant(etudiant);  // Associer l'étudiant au paiement
        paiement.setDatePaiement(LocalDate.now());  // Date du paiement
        paiement.setMontantActuel(0L);  // Initialement, aucun montant payé
        paiement.setMontantDejaPaye(0L);  // Aucun montant déjà payé au départ
        paiement.setResteEcolage(etudiant.getReduction().getMontantFinal());  // Le reste est le montant total de la scolarité
        paiement.setStatutScolarite(StatutScolarite.EN_COURS); // Statut initial

        // Sauvegarde du paiement dans la base de données
        paiementRepository.save(paiement);

        // Vérification et mise à jour du statut après le paiement
        if (paiement.getResteEcolage() == 0) {
            paiement.setStatutScolarite(StatutScolarite.SOLDE);
        } else {
            paiement.setStatutScolarite(StatutScolarite.EN_COURS);
        }

        return etudiant;

    }

    public boolean studentExists(StudentDto studentDto) {
        // Vérifier si un étudiant avec le même nom et prénom existe
        return etudiantRepository.findFirstByNomAndPrenom(studentDto.getEtudiantNom(), studentDto.getEtudiantPrenom()).isPresent();
    }

    @Transactional
    public Optional<Etudiant> getEtudiantById(Long id){
        return etudiantRepository.findByEtudiantId(id);
    }

    @Transactional
    public StudentDto updateEtudiant(String matricule, StudentDto studentDto) {
        // Récupération de l'étudiant existant par matricule
        Etudiant etudiant = etudiantRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Etudiant not found with matricule: " + matricule));

        // Mise à jour des informations de l'étudiant
        etudiant.setNom(studentDto.getEtudiantNom());
        etudiant.setPrenom(studentDto.getEtudiantPrenom());
        etudiant.setAdresse(studentDto.getEtudiantAdresse());
        etudiant.setTelephone(studentDto.getEtudiantTelephone());
        etudiant.setEmail(studentDto.getEtudiantMail());
        etudiant.setDateNaiss(studentDto.getEtudiantDateNaiss());
        etudiant.setLieuNaiss(studentDto.getEtudiantLieuNais());
        etudiant.setNationnalite(studentDto.getEtudiantNationnalite());
        etudiant.setSexe(studentDto.getEtudiantSexe());
        etudiant.setSerieBac(studentDto.getEtudiantSerieBac());
        etudiant.setAnneeBac(studentDto.getEtudiantAnneeBac());
        etudiant.setEtatProvenance(studentDto.getEtudiantEtatProvenance());
        etudiant.setDateIns(studentDto.getEtudiantDateIns());
        etudiant.setPaysBac(studentDto.getEtudiantPaysBac());
        etudiant.setAutreDiplome(studentDto.getEtudiantAutreDiplome());
        etudiant.setMentionBac(studentDto.getMentionBac());
        etudiant.setStatutboursier(studentDto.getBoursier());
        etudiant.setNiveauEtude(studentDto.getNiveauEtude());
        etudiant.setTypeModalite(studentDto.getTypeModalite());

        // Mise à jour du tuteur
        Tuteur tuteur = etudiant.getTuteur();
        tuteur.setNom(studentDto.getTuteurNom());
        tuteur.setPrenom(studentDto.getTuteurPrenom());
        tuteur.setProfession(studentDto.getTuteurProfession());
        tuteur.setOrganismeEmployeur(studentDto.getTuteurOrganismeEmployeur());
        tuteur.setAdresse(studentDto.getTuteurAdresse());
        tuteur.setTelBureau(studentDto.getTuteurTelBureau());
        tuteur.setTelDom(studentDto.getTuteurTelDom());
        tuteur.setCel(studentDto.getTuteurCel());
        tuteur.setEmail(studentDto.getTuteurEmail());
        tuteurRepository.save(tuteur);

        // Mise à jour de la filière et du parcours
        Parcours parcours = parcoursRepository.findById(studentDto.getParcoursId())
                .orElseThrow(() -> new RuntimeException("Parcours not found"));
        etudiant.setParcours(parcours);

        Filiere filiere = filiereRepository.findByNomFiliere(studentDto.getNomFiliere())
                .orElseThrow(() -> new RuntimeException("Filiere not found"));
        etudiant.setFiliere(filiere);

        // Mise à jour de la réduction et calcul du nouveau montant de scolarité
        long montantScolarite = calculerMontantScolarite(studentDto.getBoursier(), parcours.getNomParcours(), studentDto.getMentionBac());
        Reduction reduction = etudiant.getReduction();
        reduction.setMontantFinal(montantScolarite);
        reductionRepository.save(reduction);

        etudiant.setReduction(reduction);

        // Sauvegarde de l'étudiant mis à jour
        etudiant = etudiantRepository.save(etudiant);

        // Sauvegarde de l'étudiant et génération du matricule
        etudiant = etudiantRepository.save(etudiant);
        String nouveauMatricule = filiere.getNomFiliere().substring(0, 2).toUpperCase() + "-"    // Les 3 premières lettres du parcours
                + studentDto.getNiveauEtude().getCode() + "-"                                      // Le code du niveau d'étude
                + etudiant.getEtudiantId() + "-"                                                   // L'ID de l'étudiant
                + etudiant.getNom().substring(0, 2).toUpperCase();                                 // Les 2 premières lettres du nom
        etudiant.setMatricule(nouveauMatricule);
        etudiant = etudiantRepository.save(etudiant); //sauvegarde de l'étudiant avec le nouveau matricule

        // Calcul du montant déjà payé par l'étudiant
        List<Paiement> paiements = paiementRepository.findByEtudiant(etudiant);
        long montantDejaPaye = paiements.stream()
                .mapToLong(Paiement::getMontantDejaPaye)
                .sum();  // Somme de tous les paiements déjà effectués

        // Calcul du reste à payer
        long resteEcolage = montantScolarite - montantDejaPaye;


        for (Paiement paiement : paiements) {
            paiement.setResteEcolage(resteEcolage);
            paiementRepository.save(paiement);
        }

        // Conversion de l'entité mise à jour en DTO
        return convertToDto(etudiant);
    }

    @Transactional
    public void deleteEtudiant(String matricule) {
        // Récupération de l'étudiant à supprimer
        Etudiant etudiant = etudiantRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Etudiant not found"));

//        // Suppression de toutes les informations de paiement associées
//        List<Paiement> paiements = paiementRepository.findByEtudiant(etudiant);
//        for (Paiement paiement : paiements) {
//            paiementRepository.delete(paiement);
//        }

        // Suppression de la réduction associée
        Reduction reduction = etudiant.getReduction();
        if (reduction != null) {
            reductionRepository.delete(reduction);
        }

        // Suppression du tuteur associé
        Tuteur tuteur = etudiant.getTuteur();
        if (tuteur != null) {
            tuteurRepository.delete(tuteur);
        }

        // Suppression de l'étudiant
        etudiantRepository.delete(etudiant);
    }

    public StudentDto getStudentDtoByMatricule(String matricule) {
        Etudiant etudiant = etudiantRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        StudentDto dto = new StudentDto();
        dto.setEtudiantMatricule(etudiant.getMatricule());
        dto.setEtudiantNom(etudiant.getNom());
        dto.setEtudiantPrenom(etudiant.getPrenom());
        dto.setEtudiantMail(etudiant.getEmail());
        dto.setEtudiantAdresse(etudiant.getAdresse());
        dto.setEtudiantTelephone(etudiant.getTelephone());
        dto.setEtudiantDateNaiss(etudiant.getDateNaiss());
        dto.setEtudiantLieuNais(etudiant.getLieuNaiss());
        dto.setEtudiantNationnalite(etudiant.getNationnalite());
        dto.setEtudiantSexe(etudiant.getSexe());
        dto.setEtudiantSerieBac(etudiant.getSerieBac());
        dto.setEtudiantAnneeBac(etudiant.getAnneeBac());
        dto.setEtudiantEtatProvenance(etudiant.getEtatProvenance());
        dto.setEtudiantDateIns(etudiant.getDateIns());
        dto.setEtudiantPaysBac(etudiant.getPaysBac());
        dto.setEtudiantAutreDiplome(etudiant.getAutreDiplome());

        // Récupération des informations du tuteur
        if (etudiant.getTuteur() != null) {
            dto.setTuteurNom(etudiant.getTuteur().getNom());
            dto.setTuteurPrenom(etudiant.getTuteur().getPrenom());
            dto.setTuteurProfession(etudiant.getTuteur().getProfession());
            dto.setTuteurOrganismeEmployeur(etudiant.getTuteur().getOrganismeEmployeur());
            dto.setTuteurAdresse(etudiant.getTuteur().getAdresse());
            dto.setTuteurTelBureau(etudiant.getTuteur().getTelBureau());
            dto.setTuteurTelDom(etudiant.getTuteur().getTelDom());
            dto.setTuteurCel(etudiant.getTuteur().getCel());
            dto.setTuteurEmail(etudiant.getTuteur().getEmail());
        }

        // Assigner les attributs restants
        dto.setMentionBac(etudiant.getMentionBac());
        dto.setNiveauEtude(etudiant.getNiveauEtude());
        dto.setTypeModalite(etudiant.getTypeModalite());
        dto.setBoursier(etudiant.getStatutboursier());
        dto.setParcoursId(etudiant.getParcours().getParcoursId());
        dto.setNomParcours(etudiant.getParcours().getNomParcours());
        dto.setNomFiliere(etudiant.getFiliere().getNomFiliere());
        dto.setReductionMontantFinal(etudiant.getReduction().getMontantFinal());

        return dto;
    }

    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    public StudentDto convertToDto(Etudiant etudiant) {
        StudentDto dto = new StudentDto();

        dto.setEtudiantMatricule(etudiant.getMatricule());
        dto.setEtudiantNom(etudiant.getNom());
        dto.setEtudiantPrenom(etudiant.getPrenom());
        dto.setEtudiantAdresse(etudiant.getAdresse());
        dto.setEtudiantTelephone(etudiant.getTelephone());
        dto.setEtudiantMail(etudiant.getEmail());
        dto.setEtudiantDateNaiss(etudiant.getDateNaiss());
        dto.setEtudiantLieuNais(etudiant.getLieuNaiss());
        dto.setEtudiantNationnalite(etudiant.getNationnalite());
        dto.setEtudiantSexe(etudiant.getSexe());
        dto.setEtudiantSerieBac(etudiant.getSerieBac());
        dto.setEtudiantAnneeBac(etudiant.getAnneeBac());
        dto.setEtudiantEtatProvenance(etudiant.getEtatProvenance());
        dto.setEtudiantDateIns(etudiant.getDateIns());
        dto.setEtudiantPaysBac(etudiant.getPaysBac());
        dto.setEtudiantAutreDiplome(etudiant.getAutreDiplome());

        // Tuteur
        dto.setTuteurNom(etudiant.getTuteur().getNom());
        dto.setTuteurPrenom(etudiant.getTuteur().getPrenom());
        dto.setTuteurProfession(etudiant.getTuteur().getProfession());
        dto.setTuteurOrganismeEmployeur(etudiant.getTuteur().getOrganismeEmployeur());
        dto.setTuteurAdresse(etudiant.getTuteur().getAdresse());
        dto.setTuteurTelBureau(etudiant.getTuteur().getTelBureau());
        dto.setTuteurTelDom(etudiant.getTuteur().getTelDom());
        dto.setTuteurCel(etudiant.getTuteur().getCel());
        dto.setTuteurEmail(etudiant.getTuteur().getEmail());

        // Autres informations
        dto.setMentionBac(etudiant.getMentionBac());
        dto.setNiveauEtude(etudiant.getNiveauEtude());
        dto.setTypeModalite(etudiant.getTypeModalite());
        dto.setBoursier(etudiant.getStatutboursier());

        dto.setParcoursId(etudiant.getParcours().getParcoursId());
        dto.setNomParcours(etudiant.getParcours().getNomParcours());
        dto.setNomFiliere(etudiant.getFiliere().getNomFiliere());
        dto.setReductionMontantFinal(etudiant.getReduction().getMontantFinal());

        return dto;
    }

    public List<StudentDto> getEtudiantsByParcoursAndNiveau(String nomParcours, NiveauEtude niveau) {
        if (nomParcours == null || niveau == null) {
            throw new IllegalArgumentException("Le nom du parcours et le niveau ne peuvent pas être nuls.");
        }

        try {
            List<Etudiant> etudiants = etudiantRepository.findByParcoursNomParcoursAndNiveauEtude(nomParcours, niveau);

            // Convertir les entités Etudiant en StudentDto
            return etudiants.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des étudiants", e);
        }

    }

    // Méthode pour rechercher les étudiants par nom et prénom
    public List<StudentDto> searchEtudiantsByNomAndPrenom(String nom, String prenom) {
        List<Etudiant> etudiants = etudiantRepository.findByNomAndPrenom(nom, prenom);
        return etudiants.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public List<Etudiant> rechercherEtudiantsParNomOuPrenom(String recherche) {
        return etudiantRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(recherche, recherche);
    }

    ///////////////get etudiant en fonction des echeance de paiement : partiellememt et en attente ///////////////
    public List<Etudiant> getEtudiantsParStatuts(List<StatutEcheance> statuts) {
        return paiementRepository.findEtudiantsByStatutEcheance(statuts);
    }


    // Service pour récupérer les étudiants par filière et niveau d'étude (enum)
    public List<Etudiant> getEtudiantsByFiliereAndNiveauEtude(String nomFiliere, NiveauEtude niveauEtude) {
        return etudiantRepository.findAllByFiliereAndNiveauEtude(nomFiliere, niveauEtude);
    }


    public Optional<Parcours> getStatistiquesByParcours(String nomParcours) {
        return parcoursRepository.findByNomParcours(nomParcours);
    }


public List<StudentDto> getEtudiantsByFiliereAndNiveau(String nomFiliere, NiveauEtude niveauEtude) {
    List<Etudiant> etudiants = etudiantRepository.findByFiliereNomFiliereAndNiveauEtude(nomFiliere, niveauEtude); // Remplacez cette méthode par la vôtre

    return etudiants.stream()
            .map(this::convertToDto) // Assurez-vous d'avoir la méthode de conversion
            .collect(Collectors.toList());
}


    public List<ParcoursStatsDTO> getStatistiquesEtudiantsParParcours(Long parcoursId) {
        // Chercher le parcours par son ID
        Optional<Parcours> optionalParcours = parcoursRepository.findById(parcoursId);

        if (optionalParcours.isPresent()) {
            Parcours parcours = optionalParcours.get();

            // Récupérer les filières associées à ce parcours
            List<Filiere> filieres = filiereRepository.findByParcours(parcours);
            List<ParcoursStatsDTO> statsList = new ArrayList<>();

            // Récupérer les niveaux d'étude associés au parcours via le service
            List<NiveauEtude> niveauxAutorises = filiereServices.getNiveauxByParcours(parcoursId);

            // Itérer sur chaque filière
            for (Filiere filiere : filieres) {
                // Itérer sur chaque niveau autorisé
                for (NiveauEtude niveauEtude : niveauxAutorises) {
                    // Compter le nombre d'étudiants pour chaque combinaison (filière, niveau, parcours)
                    long totalEtudiants = etudiantRepository.countByFiliereAndNiveauEtudeAndParcours(filiere, niveauEtude, parcours);

                    // Compter le nombre de garçons et de filles
                    long totalGarcons = etudiantRepository.countByFiliereAndNiveauEtudeAndParcoursAndSexe(filiere, niveauEtude, parcours, "M");
                    long totalFilles = etudiantRepository.countByFiliereAndNiveauEtudeAndParcoursAndSexe(filiere, niveauEtude, parcours, "F");

                    // Créer le DTO pour chaque combinaison
                    ParcoursStatsDTO statsDTO = new ParcoursStatsDTO();
                    statsDTO.setNom(filiere.getNomFiliere());
                    statsDTO.setNiveau(niveauEtude);
                    statsDTO.setTotalGarcons(totalGarcons);
                    statsDTO.setTotalFilles(totalFilles);
                    statsDTO.setTotalEtudiants(totalEtudiants);

                    // Ajouter le DTO à la liste de statistiques
                    statsList.add(statsDTO);
                }
            }

            return statsList;
        } else {
            throw new RuntimeException("Le parcours avec l'ID " + parcoursId + " n'existe pas.");
        }
    }

}