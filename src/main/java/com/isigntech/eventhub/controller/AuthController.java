package com.isigntech.eventhub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isigntech.eventhub.dto.AuthUserResponse;
import com.isigntech.eventhub.dto.ForgotPasswordRequest;
import com.isigntech.eventhub.dto.LoginRequest;
import com.isigntech.eventhub.dto.RegisterRequest;
import com.isigntech.eventhub.dto.ResetPasswordRequest;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.service.AuthSessionService;
import com.isigntech.eventhub.service.AuthService;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthSessionService authSessionService;

    @PostMapping("/register")
    public ResponseEntity<AuthUserResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        AuthUserResponse user = authService.register(request);
        startSession(httpRequest, user.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthUserResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        AuthUserResponse user = authService.login(request);
        startSession(httpRequest, user.id());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> currentUser(HttpSession session) {
        return ResponseEntity.ok(AuthUserResponse.from(authSessionService.requireUser(session)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
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

    private void startSession(HttpServletRequest request, Long userId) {
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }
        request.getSession(true).setAttribute(AuthSessionService.USER_ID_ATTRIBUTE, userId);
    }
}
