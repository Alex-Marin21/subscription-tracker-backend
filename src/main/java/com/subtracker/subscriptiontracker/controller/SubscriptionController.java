package com.subtracker.subscriptiontracker.controller;

import com.subtracker.subscriptiontracker.entity.Subscription;
import com.subtracker.subscriptiontracker.service.SubscriptionService;
import com.subtracker.subscriptiontracker.service.RenewalAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "http://localhost:5173")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private RenewalAlertService renewalAlertService;

    @GetMapping
    public List<Subscription> getAllSubscriptions() {
        return subscriptionService.getAllSubscriptions();
    }

    @GetMapping("/{id}")
    public Subscription getSubscriptionById(@PathVariable Long id) {
        return subscriptionService.getSubscriptionById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id " + id));
    }

    @PostMapping
    public Subscription createSubscription(@RequestBody Subscription subscription) {
        return subscriptionService.createSubscription(subscription);
    }

    @PutMapping("/{id}")
    public Subscription updateSubscription(@PathVariable Long id, @RequestBody Subscription subscription) {
        return subscriptionService.updateSubscription(id, subscription);
    }

    @DeleteMapping("/{id}")
    public void deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
    }

    // Endpoint de test manual — declanșează verificarea de alerte imediat
    @PostMapping("/test-alerts")
    public String triggerAlerts() {
        renewalAlertService.triggerManualCheck();
        return "Alert check triggered. Check your email if any subscription renews in 1 or 3 days.";
    }
}
