package com.subtracker.subscriptiontracker.repository;

import com.subtracker.subscriptiontracker.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
