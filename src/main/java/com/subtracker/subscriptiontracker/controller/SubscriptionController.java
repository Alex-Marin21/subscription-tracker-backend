package com.subtracker.subscriptiontracker.controller;

import com.subtracker.subscriptiontracker.entity.Subscription;
import com.subtracker.subscriptiontracker.entity.User;
import com.subtracker.subscriptiontracker.repository.SubscriptionRepository;
import com.subtracker.subscriptiontracker.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing subscriptions linked to the authenticated user.
 */
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public SubscriptionController(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public List<Subscription> getAllSubscriptions(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return subscriptionRepository.findByUserId(user.getId());
    }

    @PostMapping
    public Subscription createSubscription(@RequestBody Subscription subscription, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        subscription.setUser(user);
        return subscriptionRepository.save(subscription);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subscription> updateSubscription(@PathVariable Long id, @RequestBody Subscription updatedSub, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return subscriptionRepository.findById(id)
                .filter(sub -> sub.getUser().getId().equals(user.getId()))
                .map(sub -> {
                    sub.setName(updatedSub.getName());
                    sub.setPrice(updatedSub.getPrice());
                    sub.setCurrency(updatedSub.getCurrency());
                    sub.setBillingCycle(updatedSub.getBillingCycle());
                    sub.setNextRenewalDate(updatedSub.getNextRenewalDate());
                    sub.setCategory(updatedSub.getCategory());
                    return ResponseEntity.ok(subscriptionRepository.save(sub));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return subscriptionRepository.findById(id)
                .filter(sub -> sub.getUser().getId().equals(user.getId()))
                .map(sub -> {
                    subscriptionRepository.delete(sub);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
