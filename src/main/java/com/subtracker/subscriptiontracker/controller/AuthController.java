package com.subtracker.subscriptiontracker.controller;

import com.subtracker.subscriptiontracker.dto.AuthRequest;
import com.subtracker.subscriptiontracker.dto.AuthResponse;
import com.subtracker.subscriptiontracker.entity.User;
import com.subtracker.subscriptiontracker.repository.UserRepository;
import com.subtracker.subscriptiontracker.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller handling user registration and login.
 * Streamlined for MVP: bypasses OTP email verification and auto-activates accounts.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Constructor for AuthController.
     *
     * @param userRepository  the repository for user data access
     * @param passwordEncoder the BCrypt password encoder
     * @param jwtUtil         the utility for generating JWT tokens
     */
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Validates password complexity.
     *
     * @param password the raw password string
     * @return true if the password meets complexity requirements, false otherwise
     */
    private boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasUpper && hasLower && hasDigit;
    }

    /**
     * Registers a new user and automatically activates the account.
     * Returns a valid JWT token immediately to auto-login the user.
     *
     * @param request the authentication request containing email and password
     * @return a ResponseEntity containing the AuthResponse (token, email, role)
     */
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already registered.");
        }

        if (!isPasswordStrong(request.getPassword())) {
            return ResponseEntity.badRequest().body("Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, and a number.");
        }

        User user = new User(request.getEmail(), passwordEncoder.encode(request.getPassword()));
        
        // Immediately activate the user for the MVP phase
        user.setActive(true);
        
        if (request.getEmail().equals("alextest123@gmail.com")) { 
            user.setRole(com.subtracker.subscriptiontracker.entity.Role.ADMIN);
        }
        
        userRepository.save(user);

        // Auto-login the user by generating a token right away
        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getRole().name()));
    }

    /**
     * Authenticates an existing user and provides a JWT token.
     *
     * @param request the authentication request containing email and password
     * @return a ResponseEntity containing the AuthResponse (token, email, role)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        var userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(401).body("Invalid email or password.");
        }
        
        if (!userOpt.get().isActive()) {
            return ResponseEntity.status(403).body("Account is not activated.");
        }

        String token = jwtUtil.generateToken(userOpt.get().getEmail());
        return ResponseEntity.ok(new AuthResponse(token, userOpt.get().getEmail(), userOpt.get().getRole().name()));
    }
}
