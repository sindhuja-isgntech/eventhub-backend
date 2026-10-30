package com.isigntech.eventhub.service;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.isigntech.eventhub.exception.AuthException;

@Service
public class JwtService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String JWT_HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final Pattern CLAIMS_PATTERN =
            Pattern.compile("^\\{\"sub\":\"([1-9][0-9]*)\",\"iat\":([0-9]+),\"exp\":([0-9]+)}$");

    private final byte[] secret;
    private final long expirationSeconds;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-seconds:3600}") long expirationSeconds) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET must be set to a secret of at least 32 bytes.");
        }
        if (expirationSeconds <= 0) {
            throw new IllegalStateException("JWT expiration must be greater than zero.");
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
    }

    public String issueToken(Long userId) {
        try {
            String encodedHeader = encode(JWT_HEADER.getBytes(StandardCharsets.UTF_8));
            Instant issuedAt = Instant.now();
            String payload = "{\"sub\":\"" + userId
                    + "\",\"iat\":" + issuedAt.getEpochSecond()
                    + ",\"exp\":" + issuedAt.plusSeconds(expirationSeconds).getEpochSecond() + "}";
            String encodedPayload = encode(payload.getBytes(StandardCharsets.UTF_8));
            String signingInput = encodedHeader + "." + encodedPayload;
            return signingInput + "." + encode(sign(signingInput));
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to create authentication token.", exception);
        }
    }

    public Long getUserId(String token) {
        try {
            String[] parts = token.split("\\.", -1);
            if (parts.length != 3 || parts[0].isBlank() || parts[1].isBlank() || parts[2].isBlank()) {
                throw unauthorized();
            }

            String signingInput = parts[0] + "." + parts[1];
            byte[] suppliedSignature = Base64.getUrlDecoder().decode(parts[2]);
            if (!MessageDigest.isEqual(sign(signingInput), suppliedSignature)) {
                throw unauthorized();
            }

            if (!encode(JWT_HEADER.getBytes(StandardCharsets.UTF_8)).equals(parts[0])) {
                throw unauthorized();
            }
            String claims = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Matcher matcher = CLAIMS_PATTERN.matcher(claims);
            if (!matcher.matches()) {
                throw unauthorized();
            }
            long subject = Long.parseLong(matcher.group(1));
            long expiration = Long.parseLong(matcher.group(3));
            if (expiration <= Instant.now().getEpochSecond() || subject <= 0) {
                throw unauthorized();
            }
            return subject;
        } catch (AuthException exception) {
            throw exception;
        } catch (IllegalArgumentException | GeneralSecurityException exception) {
            throw unauthorized();
        }
    }

    private byte[] sign(String value) throws GeneralSecurityException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
        return mac.doFinal(value.getBytes(StandardCharsets.US_ASCII));
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private AuthException unauthorized() {
        return new AuthException(HttpStatus.UNAUTHORIZED, "Authentication token is invalid or has expired.");
    }
}
