package com.isigntech.eventhub.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.isigntech.eventhub.exception.AuthException;
import com.isigntech.eventhub.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {
    public static final String USER_ID_ATTRIBUTE = "eventhub.authenticatedUserId";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (isPublicRead(request.getMethod(), path)) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            return true;
        }
        if (!authorization.startsWith(BEARER_PREFIX) || authorization.length() == BEARER_PREFIX.length()) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Authentication token is invalid or has expired.");
        }

        Long userId = jwtService.getUserId(authorization.substring(BEARER_PREFIX.length()).trim());
        request.setAttribute(USER_ID_ATTRIBUTE, userId);
        return true;
    }

    private boolean isPublicRead(String method, String path) {
        return "GET".equalsIgnoreCase(method)
                && ("/api/events".equals(path)
                        || path.startsWith("/api/events/")
                        || "/api/categories".equals(path)
                        || "/api/venues".equals(path));
    }
}
