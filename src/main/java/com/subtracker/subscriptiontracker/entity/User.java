package com.subtracker.subscriptiontracker.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entity representing an application user.
 * Contains user credentials, account status, role, and linked subscriptions.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean isActive = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscription> subscriptions;

    /**
     * Default constructor required by JPA.
     */
    public User() {}

    /**
     * Constructs a new User with the specified email and password.
     * By default, the user is created as inactive and requires OTP verification.
     *
     * @param email    the user's email address
     * @param password the user's encrypted password
     */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.isActive = false;
        this.role = Role.USER;
    }

    /**
     * Gets the user ID.
     * @return the unique identifier of the user
     */
    public Long getId() { return id; }

    /**
     * Sets the user ID.
     * @param id the unique identifier to set
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Gets the user email.
     * @return the email address
     */
    public String getEmail() { return email; }

    /**
     * Sets the user email.
     * @param email the email address to set
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Gets the user password.
     * @return the encrypted password
     */
    public String getPassword() { return password; }

    /**
     * Sets the user password.
     * @param password the encrypted password to set
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Gets the user authorization role.
     * @return the current role of the user
     */
    public Role getRole() { return role; }

    /**
     * Sets the user authorization role.
     * @param role the new role to assign
     */
    public void setRole(Role role) { this.role = role; }

    /**
     * Checks if the user account is active.
     * @return true if the account is verified and active, false otherwise
     */
    public boolean isActive() { return isActive; }

    /**
     * Sets the active status of the user account.
     * @param active the boolean status to set
     */
    public void setActive(boolean active) { isActive = active; }

    /**
     * Gets the list of subscriptions associated with this user.
     * @return the list of subscriptions
     */
    public List<Subscription> getSubscriptions() { return subscriptions; }

    /**
     * Sets the list of subscriptions for this user.
     * @param subscriptions the list of subscriptions to assign
     */
    public void setSubscriptions(List<Subscription> subscriptions) { this.subscriptions = subscriptions; }
}
