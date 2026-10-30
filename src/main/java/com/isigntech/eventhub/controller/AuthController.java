package com.isigntech.eventhub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isigntech.eventhub.dto.AuthResponse;
import com.isigntech.eventhub.dto.AuthUserResponse;
import com.isigntech.eventhub.dto.ForgotPasswordRequest;
import com.isigntech.eventhub.dto.LoginRequest;
import com.isigntech.eventhub.dto.RegisterRequest;
import com.isigntech.eventhub.dto.ResetPasswordRequest;
import com.isigntech.eventhub.service.AuthService;
import com.isigntech.eventhub.service.JwtService;
import com.isigntech.eventhub.service.RequestUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RequestUserService requestUserService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthUserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(jwtService.issueToken(user.id()), user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        AuthUserResponse user = authService.login(request);
        return ResponseEntity.ok(new AuthResponse(jwtService.issueToken(user.id()), user));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> currentUser(HttpServletRequest request) {
        return ResponseEntity.ok(AuthUserResponse.from(requestUserService.requireUser(request)));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}
