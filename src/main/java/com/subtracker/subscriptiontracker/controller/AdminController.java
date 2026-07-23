package com.subtracker.subscriptiontracker.controller;

import com.subtracker.subscriptiontracker.entity.Role;
import com.subtracker.subscriptiontracker.entity.User;
import com.subtracker.subscriptiontracker.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller strictly for administrative actions.
 * Handles fetching system statistics and managing registered users.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;

    /**
     * Constructor for AdminController.
     *
     * @param userRepository the repository for user data access
     */
    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Internal method to verify if the requester has ADMIN privileges.
     *
     * @param authentication the current security authentication object
     * @return true if the user has the ADMIN role, false otherwise
     */
    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) return false;
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(user -> user.getRole() == Role.ADMIN)
                .orElse(false);
    }

    /**
     * Retrieves global platform statistics.
     *
     * @param authentication the current security authentication object
     * @return a ResponseEntity containing the total users and total subscriptions
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(Authentication authentication) {
        if (!isAdmin(authentication)) return ResponseEntity.status(403).body("Access denied.");
        
        List<User> users = userRepository.findAll();
        long totalUsers = users.size();
        long totalSubscriptions = users.stream().mapToLong(u -> u.getSubscriptions().size()).sum();
        
        return ResponseEntity.ok(Map.<String, Object>of(
            "totalUsers", totalUsers,
            "totalSubscriptions", totalSubscriptions
        ));
    }

    /**
     * Retrieves a list of all registered users with their details.
     *
     * @param authentication the current security authentication object
     * @return a ResponseEntity containing a list of user details mapped to objects
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        if (!isAdmin(authentication)) return ResponseEntity.status(403).body("Access denied.");
        
        List<Map<String, Object>> userList = userRepository.findAll().stream().map(u -> 
            Map.<String, Object>of(
                "id", u.getId(),
                "email", u.getEmail(),
                "role", u.getRole().name(),
                "isActive", u.isActive(),
                "subscriptionCount", u.getSubscriptions().size()
            )
        ).collect(Collectors.toList());

        return ResponseEntity.ok(userList);
    }

    /**
     * Deletes a specific user from the platform.
     *
     * @param id the unique identifier of the user to delete
     * @param authentication the current security authentication object
     * @return a ResponseEntity indicating the result of the deletion operation
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        if (!isAdmin(authentication)) return ResponseEntity.status(403).body("Access denied.");
        
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.<String, Object>of("message", "User deleted successfully."));
    }
}
