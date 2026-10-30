package com.isigntech.eventhub.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.isigntech.eventhub.config.JwtAuthenticationInterceptor;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.exception.AuthException;
import com.isigntech.eventhub.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestUserService {
    private final UserRepository userRepository;

    public User requireUser(HttpServletRequest request) {
        Object userId = request.getAttribute(JwtAuthenticationInterceptor.USER_ID_ATTRIBUTE);
        if (!(userId instanceof Long id)) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Please sign in to continue.");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, "Please sign in to continue."));
    }

    public User requireAdmin(HttpServletRequest request) {
        User user = requireUser(request);
        if (!"ADMIN".equals(user.getRole())) {
            throw new AuthException(HttpStatus.FORBIDDEN, "Administrator access is required.");
        }
        return user;
    }
}
