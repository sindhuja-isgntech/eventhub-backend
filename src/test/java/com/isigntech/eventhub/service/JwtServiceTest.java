package com.isigntech.eventhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.isigntech.eventhub.exception.AuthException;

class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "local-test-secret-that-has-more-than-32-bytes",
                3600);
    }

    @Test
    void issuedTokenContainsTheUserIdAndVerifies() {
        String token = jwtService.issueToken(42L);

        assertEquals(42L, jwtService.getUserId(token));
    }

    @Test
    void rejectsTamperedToken() {
        String token = jwtService.issueToken(42L);
        String tamperedToken = token.substring(0, token.length() - 1)
                + (token.endsWith("a") ? "b" : "a");

        assertThrows(AuthException.class, () -> jwtService.getUserId(tamperedToken));
    }

    @Test
    void rejectsSecretsShorterThanThirtyTwoBytes() {
        assertThrows(IllegalStateException.class,
                () -> new JwtService("too-short", 3600));
    }
}
