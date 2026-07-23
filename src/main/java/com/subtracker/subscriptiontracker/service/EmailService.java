package com.subtracker.subscriptiontracker.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service handling email notifications for subscription renewals.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRenewalAlert(String toEmail, String subscriptionName, String renewalDate, Double price, String currency) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Abonamentul tău " + subscriptionName + " expiră curând!");
        message.setText("Salut,\n\nAbonamentul tău " + subscriptionName + " (" + price + " " + currency + ") se va reînnoi pe data de " + renewalDate + ".\n\nAsigură-te că ai fonduri suficiente pe card!\n\nEchipa SubTrack");

        mailSender.send(message);
    }
}
