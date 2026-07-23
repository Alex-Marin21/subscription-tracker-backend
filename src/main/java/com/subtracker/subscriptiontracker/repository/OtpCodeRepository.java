package com.subtracker.subscriptiontracker.repository;

import com.subtracker.subscriptiontracker.entity.OtpCode;
import com.subtracker.subscriptiontracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for OtpCode database operations.
 */
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    
    /**
     * Retrieves an OTP record based on the associated user entity.
     *
     * @param user the user whose OTP is being queried
     * @return an Optional containing the OtpCode if found
     */
    Optional<OtpCode> findByUser(User user);
    
    /**
     * Deletes the OTP record associated with a specific user.
     *
     * @param user the user whose OTP record should be removed
     */
    void deleteByUser(User user);
}
