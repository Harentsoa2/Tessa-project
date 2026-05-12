package com.hei.school.tessaproject.controller;

import com.hei.school.tessaproject.dto.LoginRequest;
import com.hei.school.tessaproject.dto.RegisterRequest;
import com.hei.school.tessaproject.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final String googleFailureUrl;

    public AuthController(
            AuthService authService,
            @Value("${tessa.frontend-google-callback-url}") String googleFailureUrl) {
        this.authService = authService;
        this.googleFailureUrl = googleFailureUrl;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User created successfully"));
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        return Map.of(
                "message", "Logged in successfully",
                "user", authService.login(request, servletRequest));
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest request) {
        authService.logout(request);
        return Map.of("message", "Logged out successfully");
    }

    @GetMapping("/google")
    public void google(HttpServletResponse response) throws IOException {
        response.sendRedirect(googleFailureUrl + "?status=failure");
    }

    @GetMapping("/google/callback")
    public void googleCallback(HttpServletResponse response) throws IOException {
        response.sendRedirect(googleFailureUrl + "?status=failure");
    }
}
