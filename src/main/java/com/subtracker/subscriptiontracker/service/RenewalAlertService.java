package com.subtracker.subscriptiontracker.service;

import com.subtracker.subscriptiontracker.entity.Subscription;
import com.subtracker.subscriptiontracker.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RenewalAlertService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // Rulează în fiecare zi la 08:00 dimineața
    @Scheduled(cron = "0 0 8 * * *")
    public void checkUpcomingRenewals() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Subscription sub : subscriptions) {
            long daysUntil = ChronoUnit.DAYS.between(today, sub.getNextRenewalDate());
            if (daysUntil == 3 || daysUntil == 1) {
                sendRenewalEmail(sub, daysUntil);
            }
        }
    }

    private void sendRenewalEmail(Subscription sub, long daysUntil) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(fromEmail); // MVP: trimitem la propriul email; ulterior legăm de user
        message.setSubject("⏰ " + sub.getName() + " renews in " + daysUntil + " day(s)");
        message.setText(
            "Hey!\n\n" +
            "Your subscription \"" + sub.getName() + "\" (" + sub.getPrice() + " " + sub.getCurrency() + ") " +
            "renews on " + sub.getNextRenewalDate() + ".\n\n" +
            "Category: " + sub.getCategory() + "\n\n" +
            "— SubTrack"
        );
        mailSender.send(message);
    }

    // Endpoint de test manual, ca să nu aștepți până la 08:00
    public void triggerManualCheck() {
        checkUpcomingRenewals();
    }
}
