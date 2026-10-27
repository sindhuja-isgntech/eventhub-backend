package com.isigntech.eventhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.exception.AuthException;
import com.isigntech.eventhub.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

class AuthSessionServiceTest {
    private UserRepository userRepository;
    private HttpSession session;
    private AuthSessionService authSessionService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        session = mock(HttpSession.class);
        authSessionService = new AuthSessionService(userRepository);
    }

    @Test
    void rejectsRequestsWithoutSignedInUser() {
        when(session.getAttribute(AuthSessionService.USER_ID_ATTRIBUTE)).thenReturn(null);

        AuthException exception = assertThrows(
                AuthException.class,
                () -> authSessionService.requireUser(session));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void rejectsNonAdminFromAdminEndpoints() {
        User customer = User.builder().id(12L).role("CUSTOMER").build();
        when(session.getAttribute(AuthSessionService.USER_ID_ATTRIBUTE)).thenReturn(12L);
        when(userRepository.findById(12L)).thenReturn(Optional.of(customer));

        AuthException exception = assertThrows(
                AuthException.class,
                () -> authSessionService.requireAdmin(session));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void returnsAdminForAdminSession() {
        User admin = User.builder().id(13L).role("ADMIN").build();
        when(session.getAttribute(AuthSessionService.USER_ID_ATTRIBUTE)).thenReturn(13L);
        when(userRepository.findById(13L)).thenReturn(Optional.of(admin));

        assertEquals(admin, authSessionService.requireAdmin(session));
    }
}
