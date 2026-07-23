package com.subtracker.subscriptiontracker.controller;

import com.subtracker.subscriptiontracker.dto.AuthRequest;
import com.subtracker.subscriptiontracker.dto.AuthResponse;
import com.subtracker.subscriptiontracker.entity.OtpCode;
import com.subtracker.subscriptiontracker.entity.User;
import com.subtracker.subscriptiontracker.repository.OtpCodeRepository;
import com.subtracker.subscriptiontracker.repository.UserRepository;
import com.subtracker.subscriptiontracker.security.JwtUtil;
import com.subtracker.subscriptiontracker.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthController(UserRepository userRepository, OtpCodeRepository otpCodeRepository, 
                          PasswordEncoder passwordEncoder, JwtUtil jwtUtil, EmailService emailService) {
        this.userRepository = userRepository;
        this.otpCodeRepository = otpCodeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

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

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

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
        
        // IMPORTANT: Înlocuiește cu adresa ta reală dacă dorești să fii admin automat.
        if (request.getEmail().equals("alextest123@gmail.com")) { 
            user.setRole(com.subtracker.subscriptiontracker.entity.Role.ADMIN);
        }
        
        userRepository.save(user);

        String code = generateOtp();
        OtpCode otpCode = new OtpCode(code, LocalDateTime.now().plusMinutes(15), user);
        otpCodeRepository.save(otpCode);

        emailService.sendOtpEmail(user.getEmail(), code);

        return ResponseEntity.ok(Map.of("message", "Registration successful. Please check your email for the verification code."));
    }

    @PostMapping("/verify-account")
    @Transactional
    public ResponseEntity<?> verifyAccount(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found.");

        User user = userOpt.get();
        if (user.isActive()) return ResponseEntity.badRequest().body("Account is already active.");

        Optional<OtpCode> otpOpt = otpCodeRepository.findByUser(user);
        if (otpOpt.isEmpty() || !otpOpt.get().getCode().equals(code)) {
            return ResponseEntity.badRequest().body("Invalid verification code.");
        }

        if (otpOpt.get().getExpirationTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Verification code expired.");
        }

        user.setActive(true);
        userRepository.save(user);
        otpCodeRepository.delete(otpOpt.get());

        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getRole().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        var userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(401).body("Invalid email or password.");
        }
        
        if (!userOpt.get().isActive()) {
            return ResponseEntity.status(403).body("Account is not activated. Please verify your email.");
        }

        String token = jwtUtil.generateToken(userOpt.get().getEmail());
        return ResponseEntity.ok(new AuthResponse(token, userOpt.get().getEmail(), userOpt.get().getRole().name()));
    }

    @PostMapping("/forgot-password")
    @Transactional
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            otpCodeRepository.findByUser(user).ifPresent(otpCodeRepository::delete);
            
            String code = generateOtp();
            OtpCode otpCode = new OtpCode(code, LocalDateTime.now().plusMinutes(15), user);
            otpCodeRepository.save(otpCode);
            emailService.sendOtpEmail(user.getEmail(), code);
        }
        return ResponseEntity.ok(Map.of("message", "If an account with this email exists, a reset code has been sent."));
    }

    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String newPassword = request.get("newPassword");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("Invalid request.");

        User user = userOpt.get();
        Optional<OtpCode> otpOpt = otpCodeRepository.findByUser(user);
        
        if (otpOpt.isEmpty() || !otpOpt.get().getCode().equals(code) || otpOpt.get().getExpirationTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Invalid or expired reset code.");
        }

        if (!isPasswordStrong(newPassword)) {
            return ResponseEntity.badRequest().body("Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, and a number.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        otpCodeRepository.delete(otpOpt.get());

        return ResponseEntity.ok(Map.of("message", "Password has been successfully reset."));
    }
}
