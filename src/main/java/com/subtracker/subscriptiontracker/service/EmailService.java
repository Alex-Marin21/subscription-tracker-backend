package com.subtracker.subscriptiontracker.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service responsible for dispatching email notifications asynchronously.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRenewalAlert(String toEmail, String subscriptionName, String renewalDate, Double price, String currency) {
        CompletableFuture.runAsync(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setSubject("Upcoming Renewal: " + subscriptionName);
                message.setText("Hello,\n\nYour subscription for " + subscriptionName + " (" + price + " " + currency + ") will renew on " + renewalDate + ".\n\nPlease ensure you have sufficient funds available.\n\nSubTrack Team");

                mailSender.send(message);
            } catch (Exception e) {
                System.err.println("Error sending renewal email to " + toEmail + ": " + e.getMessage());
            }
        });
    }

    public void sendOtpEmail(String toEmail, String otpCode) {
        // Trimitere exclusivă prin e-mail, asincron
        CompletableFuture.runAsync(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setSubject("Your SubTrack Verification Code");
                message.setText("Hello,\n\nYour verification code is: " + otpCode + "\n\nThis code will expire in 15 minutes. Please do not share it with anyone.\n\nSubTrack Team");

                mailSender.send(message);
            } catch (Exception e) {
                System.err.println("SMTP Error: Could not send OTP email to " + toEmail + ". " + e.getMessage());
            }
        });
    }
}
