package com.defitech.GestUni.service.BEDJRA;

import com.defitech.GestUni.models.BEDJRA.Echeance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender; // Configurer JavaMailSender dans application.properties

    public void envoyerEmailRappel(String emailTuteur, Echeance echeance) {
        // Vérification des paramètres
        if (emailTuteur == null || emailTuteur.isEmpty()) {
            throw new IllegalArgumentException("L'email du tuteur ne peut pas être vide.");
        }
        if (echeance == null || echeance.getEtudiant() == null) {
            throw new IllegalArgumentException("Les informations d'échéance ne peuvent pas être nulles.");
        }

        // Création du message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailTuteur);
        message.setSubject("Rappel de Paiement");
        message.setText("Bonjour,\n\n" +
                "Ceci est un rappel concernant l'échéance de paiement pour l'étudiant " +
                echeance.getEtudiant().getNom() + " " + echeance.getEtudiant().getPrenom() +
                ".\n\n" +
                "Détails de l'échéance :\n" +
                "Montant : " + echeance.getMontantParEcheance() + "\n" +
                "Date : " + echeance.getDateEcheance() + "\n\n" +
                "Merci de procéder au paiement avant la date limite.\n\n" +
                "Cordialement,\n" +
                "L'équipe de gestion des paiements de l'institut Polytechnique DEFITECH");

        mailSender.send(message);
    }


    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("armelbedjra12@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            System.out.println("Email envoyé avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }
}