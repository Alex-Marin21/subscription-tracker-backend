package com.subtracker.subscriptiontracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a One-Time Password (OTP) used for account activation and password recovery.
 */
@Entity
@Table(name = "otp_codes")
public class OtpCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expirationTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Default constructor required by JPA.
     */
    public OtpCode() {}

    /**
     * Constructs a new OTP code mapped to a specific user.
     *
     * @param code           the generated OTP string
     * @param expirationTime the timestamp when the code becomes invalid
     * @param user           the associated user entity
     */
    public OtpCode(String code, LocalDateTime expirationTime, User user) {
        this.code = code;
        this.expirationTime = expirationTime;
        this.user = user;
    }

    /**
     * Gets the OTP ID.
     * @return the unique identifier of the OTP record
     */
    public Long getId() { return id; }

    /**
     * Sets the OTP ID.
     * @param id the unique identifier to set
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Gets the OTP string value.
     * @return the generated code
     */
    public String getCode() { return code; }

    /**
     * Sets the OTP string value.
     * @param code the code to set
     */
    public void setCode(String code) { this.code = code; }

    /**
     * Gets the expiration timestamp of the OTP.
     * @return the local date time of expiration
     */
    public LocalDateTime getExpirationTime() { return expirationTime; }

    /**
     * Sets the expiration timestamp.
     * @param expirationTime the timestamp to set
     */
    public void setExpirationTime(LocalDateTime expirationTime) { this.expirationTime = expirationTime; }

    /**
     * Gets the associated user entity.
     * @return the linked user
     */
    public User getUser() { return user; }

    /**
     * Sets the associated user entity.
     * @param user the user to map to this OTP
     */
    public void setUser(User user) { this.user = user; }
}
