package com.project.wms.auth.service;

import com.project.wms.auth.config.JwtProperties;
import com.project.wms.auth.entity.RefreshToken;
import com.project.wms.auth.entity.User;
import com.project.wms.auth.exception.InvalidRefreshTokenException;
import com.project.wms.auth.repository.RefreshTokenRepository;
import com.project.wms.auth.security.TokenHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHasher tokenHasher;
    private final JwtProperties jwtProperties;

    //Issues a brand new refresh token for a user (login, or first-time
    // issuance during rotation) and persists only its hash.
    @Transactional
    public String issue(User user) {
        String rawToken = tokenHasher.generateOpaqueToken();

        RefreshToken entity = new RefreshToken();
        entity.setUser(user);
        entity.setTokenHash(tokenHasher.hash(rawToken));
        entity.setExpiresAt(Instant.now().plusSeconds(jwtProperties.refreshTokenExpirationDays() * 86400));
        refreshTokenRepository.save(entity);

        return rawToken; // raw value goes to the client cookie — never stored
    }

    @Transactional
    public RotationResult rotate(String rawToken) {
        // 1. Hash and check in DB.
        String hash = tokenHasher.hash(rawToken);

        RefreshToken existing = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not recognized"));

        if (!existing.isActive()) {
            throw new InvalidRefreshTokenException("Refresh token expired or already revoked");
        }

        if (!existing.getUser().isActive()) {
            throw new InvalidRefreshTokenException("Account is deactivated");
        }

        // 2. generate new raw token and hash it.
        String newRawToken = tokenHasher.generateOpaqueToken();
        String newHash = tokenHasher.hash(newRawToken);

        // 3. rotate the refresh token, set the same user and save it.
        RefreshToken rotated = new RefreshToken();
        rotated.setUser(existing.getUser());
        rotated.setTokenHash(newHash);
        rotated.setExpiresAt(Instant.now().plusSeconds(jwtProperties.refreshTokenExpirationDays() * 86400));
        refreshTokenRepository.save(rotated);

        // 4. Revoke the previous token with current time and replacedBy field as new Hashed token
        existing.setRevokedAt(Instant.now());
        existing.setReplacedBy(newHash);
        refreshTokenRepository.save(existing);

        return new RotationResult(existing.getUser(), newRawToken);
    }

    @Transactional
    public void revoke(String rawToken) {
        String hash = tokenHasher.hash(rawToken);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(rt -> {
            rt.setRevokedAt(Instant.now());
            refreshTokenRepository.save(rt);
        });
    }

    public record RotationResult(User user, String newRawToken) {}
}