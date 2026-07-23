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
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Internal method to verify if the requester has ADMIN privileges.
     */
    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) return false;
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(user -> user.getRole() == Role.ADMIN)
                .orElse(false);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(Authentication authentication) {
        if (!isAdmin(authentication)) return ResponseEntity.status(403).body("Access denied.");
        
        List<User> users = userRepository.findAll();
        long totalUsers = users.size();
        long totalSubscriptions = users.stream().mapToLong(u -> u.getSubscriptions().size()).sum();
        
        return ResponseEntity.ok(Map.of(
            "totalUsers", totalUsers,
            "totalSubscriptions", totalSubscriptions
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        if (!isAdmin(authentication)) return ResponseEntity.status(403).body("Access denied.");
        
        List<Map<String, Object>> userList = userRepository.findAll().stream().map(u -> Map.of(
            "id", u.getId(),
            "email", u.getEmail(),
            "role", u.getRole().name(),
            "isActive", u.isActive(),
            "subscriptionCount", u.getSubscriptions().size()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(userList);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        if (!isAdmin(authentication)) return ResponseEntity.status(403).body("Access denied.");
        
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully."));
    }
}
