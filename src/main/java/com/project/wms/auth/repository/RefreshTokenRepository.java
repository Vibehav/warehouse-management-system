package com.project.wms.auth.repository;


import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.wms.auth.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshToken token SET token.revokedAt = :revokedAt " +
            "WHERE token.user.id = :userId AND token.revokedAt IS NULL")
    void revokeActiveByUserId(@Param("userId") Long userId, @Param("revokedAt") Instant revokedAt);

}