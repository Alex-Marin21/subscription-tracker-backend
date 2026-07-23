package com.subtracker.subscriptiontracker.service;

import com.subtracker.subscriptiontracker.entity.Subscription;
import com.subtracker.subscriptiontracker.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    public Subscription createSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    public Subscription updateSubscription(Long id, Subscription updatedSubscription) {
        return subscriptionRepository.findById(id).map(subscription -> {
            subscription.setName(updatedSubscription.getName());
            subscription.setPrice(updatedSubscription.getPrice());
            subscription.setCurrency(updatedSubscription.getCurrency());
            subscription.setBillingCycle(updatedSubscription.getBillingCycle());
            subscription.setNextRenewalDate(updatedSubscription.getNextRenewalDate());
            subscription.setCategory(updatedSubscription.getCategory());
            return subscriptionRepository.save(subscription);
        }).orElseThrow(() -> new RuntimeException("Subscription not found with id " + id));
    }

    public void deleteSubscription(Long id) {
        subscriptionRepository.deleteById(id);
    }
}
