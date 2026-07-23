package com.subtracker.subscriptiontracker.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service responsible for dispatching email notifications, including renewals and authentication codes.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Constructs the EmailService with the injected JavaMailSender.
     *
     * @param mailSender the Spring Framework email sender instance
     */
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email alert notifying the user of an upcoming subscription renewal.
     *
     * @param toEmail          the recipient email address
     * @param subscriptionName the name of the renewing service
     * @param renewalDate      the formatted date of renewal
     * @param price            the cost of the subscription
     * @param currency         the currency code
     */
    public void sendRenewalAlert(String toEmail, String subscriptionName, String renewalDate, Double price, String currency) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Upcoming Renewal: " + subscriptionName);
        message.setText("Hello,\n\nYour subscription for " + subscriptionName + " (" + price + " " + currency + ") will renew on " + renewalDate + ".\n\nPlease ensure you have sufficient funds available.\n\nSubTrack Team");

        mailSender.send(message);
    }

    /**
     * Sends a One-Time Password (OTP) code for account verification or password reset.
     *
     * @param toEmail the recipient email address
     * @param otpCode the generated 6-digit OTP code
     */
    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your SubTrack Verification Code");
        message.setText("Hello,\n\nYour verification code is: " + otpCode + "\n\nThis code will expire in 15 minutes. Please do not share it with anyone.\n\nSubTrack Team");

        mailSender.send(message);
    }
}
