package com.isigntech.eventhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.isigntech.eventhub.config.JwtAuthenticationInterceptor;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.exception.AuthException;
import com.isigntech.eventhub.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

class RequestUserServiceTest {
    private UserRepository userRepository;
    private HttpServletRequest request;
    private RequestUserService requestUserService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        request = mock(HttpServletRequest.class);
        requestUserService = new RequestUserService(userRepository);
    }

    @Test
    void rejectsRequestsWithoutVerifiedTokenIdentity() {
        when(request.getAttribute(JwtAuthenticationInterceptor.USER_ID_ATTRIBUTE)).thenReturn(null);

        AuthException exception = assertThrows(AuthException.class,
                () -> requestUserService.requireUser(request));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void rejectsCustomerFromAdminEndpoint() {
        User customer = User.builder().id(12L).role("CUSTOMER").build();
        when(request.getAttribute(JwtAuthenticationInterceptor.USER_ID_ATTRIBUTE)).thenReturn(12L);
        when(userRepository.findById(12L)).thenReturn(Optional.of(customer));

        AuthException exception = assertThrows(AuthException.class,
                () -> requestUserService.requireAdmin(request));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void resolvesAdminFromCurrentDatabaseRole() {
        User admin = User.builder().id(13L).role("ADMIN").build();
        when(request.getAttribute(JwtAuthenticationInterceptor.USER_ID_ATTRIBUTE)).thenReturn(13L);
        when(userRepository.findById(13L)).thenReturn(Optional.of(admin));

        assertEquals(admin, requestUserService.requireAdmin(request));
    }
}
