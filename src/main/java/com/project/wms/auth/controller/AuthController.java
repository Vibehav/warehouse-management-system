package com.project.wms.auth.controller;

import com.project.wms.auth.dto.*;
import com.project.wms.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final long REFRESH_COOKIE_MAX_AGE_SECONDS = 14 * 24 * 60 * 60; // 14 days, matches config

    @PostMapping("/register")
    public ResponseEntity<AccessTokenResponseDto> register(@RequestBody RegisterRequestDto request) {
        TokenPairResponse tokens = authService.register(request);
        return respondWithTokens(tokens);
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponseDto> login(@RequestBody LoginRequestDto request) {
        TokenPairResponse tokens = authService.login(request);
        return respondWithTokens(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponseDto> refresh(
            @CookieValue(name = REFRESH_COOKIE_NAME) String refreshToken) {
        TokenPairResponse tokens = authService.refresh(refreshToken);
        return respondWithTokens(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout( @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken) {
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        ResponseCookie clearCookie = ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false) // if prod set it as true
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header("Set-Cookie", clearCookie.toString())
                .build();
    }

    private ResponseEntity<AccessTokenResponseDto> respondWithTokens(TokenPairResponse tokens) {
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE_NAME, tokens.refreshToken())
                .httpOnly(true)   // JavaScript cannot read this — mitigates XSS token theft
                .secure(false)     // sent over HTTPS -> true / sent over HTTP -> false
                .sameSite("Lax")  // mitigates CSRF while still allowing top-level navigation
                .path("/api/auth") // only sent back to auth endpoints, not the whole API
                .maxAge(REFRESH_COOKIE_MAX_AGE_SECONDS)
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", refreshCookie.toString())
                .body(new AccessTokenResponseDto(tokens.accessToken()));
    }
}