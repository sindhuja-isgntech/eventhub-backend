package com.isigntech.eventhub.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.isigntech.eventhub.dto.AuthUserResponse;
import com.isigntech.eventhub.dto.ForgotPasswordRequest;
import com.isigntech.eventhub.dto.LoginRequest;
import com.isigntech.eventhub.dto.RegisterRequest;
import com.isigntech.eventhub.dto.ResetPasswordRequest;
import com.isigntech.eventhub.entity.PasswordResetToken;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.exception.AuthException;
import com.isigntech.eventhub.repository.PasswordResetTokenRepository;
import com.isigntech.eventhub.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final String frontendUrl;
    private final String mailFrom;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            @Value("${app.frontend-url}") String frontendUrl,
            @Value("${spring.mail.username:}") String mailFrom) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.frontendUrl = frontendUrl;
        this.mailFrom = mailFrom;
    }

    @Transactional
    public AuthUserResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new AuthException(HttpStatus.CONFLICT, "An account with this email already exists.");
        }

        User user = User.builder()
                .name(request.name().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .role(request.role())
                .build();
        try {
            return AuthUserResponse.from(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException exception) {
            throw new AuthException(HttpStatus.CONFLICT, "An account with this email already exists.");
        }
    }

    @Transactional
    public AuthUserResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));

        String storedPassword = user.getPassword();
        boolean isBcryptHash = storedPassword.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
        boolean passwordMatches = isBcryptHash
                ? passwordEncoder.matches(request.password(), storedPassword)
                : MessageDigest.isEqual(
                        request.password().getBytes(StandardCharsets.UTF_8),
                        storedPassword.getBytes(StandardCharsets.UTF_8));

        if (!passwordMatches) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }

        if (!isBcryptHash) {
            user.setPassword(passwordEncoder.encode(request.password()));
            userRepository.save(user);
        }

        return AuthUserResponse.from(user);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmailIgnoreCase(normalizeEmail(request.email())).ifPresent(user -> {
            byte[] tokenBytes = new byte[32];
            secureRandom.nextBytes(tokenBytes);
            String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
            String tokenHash = hashToken(rawToken);

            passwordResetTokenRepository.deleteByUser_Id(user.getId());
            passwordResetTokenRepository.save(PasswordResetToken.builder()
                    .tokenHash(tokenHash)
                    .expiresAt(LocalDateTime.now().plusMinutes(15))
                    .user(user)
                    .build());

            String resetUrl = UriComponentsBuilder
                    .fromUriString(frontendUrl.replaceAll("/+$", ""))
                    .path("/reset-password")
                    .queryParam("token", rawToken)
                    .build()
                    .encode()
                    .toUriString();
            SimpleMailMessage message = new SimpleMailMessage();
            if (!mailFrom.isBlank()) {
                message.setFrom(mailFrom);
            }
            message.setTo(user.getEmail());
            message.setSubject("Reset your EventHub password");
            message.setText("We received a request to reset your EventHub password. "
                    + "Use the link below within 15 minutes to choose a new password:\n\n"
                    + resetUrl
                    + "\n\nIf you did not request this, you can ignore this email.");
            try {
                mailSender.send(message);
            } catch (MailException exception) {
                throw new AuthException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "We couldn't send the password reset email. Please try again later.");
            }
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String tokenHash = hashToken(request.token());
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByTokenHashAndExpiresAtAfter(tokenHash, LocalDateTime.now())
                .orElseThrow(() -> new AuthException(
                        HttpStatus.BAD_REQUEST,
                        "This password reset link is invalid or has expired."));

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        passwordResetTokenRepository.deleteByUser_Id(user.getId());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        }
    }
}
