package com.subtracker.subscriptiontracker.repository;

import com.subtracker.subscriptiontracker.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for Subscription entity database operations.
 */
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserId(Long userId);
}
