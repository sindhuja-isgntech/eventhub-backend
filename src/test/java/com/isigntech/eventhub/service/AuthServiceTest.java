package com.isigntech.eventhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.isigntech.eventhub.dto.AuthUserResponse;
import com.isigntech.eventhub.dto.ForgotPasswordRequest;
import com.isigntech.eventhub.dto.LoginRequest;
import com.isigntech.eventhub.dto.ResetPasswordRequest;
import com.isigntech.eventhub.entity.PasswordResetToken;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.repository.PasswordResetTokenRepository;
import com.isigntech.eventhub.repository.UserRepository;

class AuthServiceTest {
    private UserRepository userRepository;
    private PasswordResetTokenRepository resetTokenRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private JavaMailSender mailSender;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        resetTokenRepository = mock(PasswordResetTokenRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        mailSender = mock(JavaMailSender.class);
        authService = new AuthService(
                userRepository,
                resetTokenRepository,
                passwordEncoder,
                mailSender,
                "http://localhost:5173",
                "noreply@example.com");
    }

    @Test
    void loginMigratesLegacyPlaintextPasswordToBcrypt() {
        User user = User.builder()
                .id(42L)
                .name("Taylor User")
                .email("taylor@example.com")
                .password("my-password")
                .role("CUSTOMER")
                .build();
        when(userRepository.findByEmailIgnoreCase("taylor@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthUserResponse response = authService.login(new LoginRequest("TAYLOR@example.com", "my-password"));

        assertEquals(42L, response.id());
        assertEquals("CUSTOMER", response.role());
        assertTrue(passwordEncoder.matches("my-password", user.getPassword()));
        verify(userRepository).save(user);
    }

    @Test
    void forgotPasswordEmailsResetLinkAndStoresOnlyItsHash() throws Exception {
        User user = User.builder().id(10L).email("taylor@example.com").build();
        when(userRepository.findByEmailIgnoreCase("taylor@example.com")).thenReturn(Optional.of(user));

        authService.forgotPassword(new ForgotPasswordRequest("taylor@example.com"));

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(resetTokenRepository).save(tokenCaptor.capture());
        PasswordResetToken savedToken = tokenCaptor.getValue();
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        String messageBody = messageCaptor.getValue().getText();
        String resetUrl = messageBody.lines()
                .filter(line -> line.startsWith("http://localhost:5173/reset-password?token="))
                .findFirst()
                .orElseThrow();
        String rawToken = resetUrl.substring(resetUrl.indexOf("token=") + "token=".length());
        String expectedHash = HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(rawToken.getBytes(StandardCharsets.UTF_8)));

        assertEquals(expectedHash, savedToken.getTokenHash());
        assertTrue(savedToken.getExpiresAt().isAfter(LocalDateTime.now()));
        assertEquals("noreply@example.com", messageCaptor.getValue().getFrom());
    }

    @Test
    void forgotPasswordDoesNotRevealUnknownEmails() {
        when(userRepository.findByEmailIgnoreCase("unknown@example.com")).thenReturn(Optional.empty());

        authService.forgotPassword(new ForgotPasswordRequest("unknown@example.com"));

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
        verify(resetTokenRepository, never()).save(any(PasswordResetToken.class));
    }

    @Test
    void resetPasswordUpdatesHashAndConsumesResetToken() throws Exception {
        User user = User.builder().id(19L).email("taylor@example.com").password("old-hash").build();
        String rawToken = "one-time-token";
        String tokenHash = HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(rawToken.getBytes(StandardCharsets.UTF_8)));
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        when(resetTokenRepository.findByTokenHashAndExpiresAtAfter(eq(tokenHash), any(LocalDateTime.class)))
                .thenReturn(Optional.of(resetToken));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.resetPassword(new ResetPasswordRequest(rawToken, "new-password"));

        assertTrue(passwordEncoder.matches("new-password", user.getPassword()));
        verify(resetTokenRepository).deleteByUser_Id(19L);
    }
}
