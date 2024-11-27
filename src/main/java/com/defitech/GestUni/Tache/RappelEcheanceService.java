//package com.defitech.GestUni.Tache;
//
//
//import com.defitech.GestUni.enums.BEDJRA.StatutEcheance;
//import com.defitech.GestUni.models.BEDJRA.Echeance;
//import com.defitech.GestUni.models.Bases.Etudiant;
//import com.defitech.GestUni.repository.BEDJRA.EcheanceRepository;
//import com.defitech.GestUni.repository.BEDJRA.StudentRepository;
//import com.defitech.GestUni.service.BEDJRA.EmailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//
//import java.time.LocalDate;
//import java.util.List;
//
//public class RappelEcheanceService {
//    @Autowired
//    private EcheanceRepository echeanceRepository;
//
//    @Autowired
//    private StudentRepository etudiantRepository;
//
//    @Autowired
//    private EmailService emailService; // Service pour envoyer les emails
//
//    @Scheduled(cron = "0 0 1 * * ?") // Exécution quotidienne à 1h du matin
//    public void verifierEtEnvoyerRappels() {
//        LocalDate aujourdHui = LocalDate.now();
//        LocalDate seuilDate = aujourdHui.plusDays(14); // Date limite pour le rappel
//
//        List<Echeance> echeances = echeanceRepository.findAll();
//
//        for (Echeance echeance : echeances) {
//            if (echeance.getStatut() != StatutEcheance.PAYEE &&
//                    echeance.getDateEcheance().isBefore(seuilDate)) {
//
//
//                Etudiant etudiant = echeance.getEtudiant();
//                String emailTuteur = etudiant.getTuteur().getEmail();
//
//                if (emailTuteur != null) {
//                    emailService.envoyerEmailRappel(emailTuteur, echeance);
//                }
//            }
//        }
//    }
//}