package com.project.wms.auth.service;


import com.project.wms.auth.dto.LoginRequestDto;
import com.project.wms.auth.dto.RegisterRequestDto;
import com.project.wms.auth.dto.TokenPairResponse;
import com.project.wms.auth.entity.Permission;
import com.project.wms.auth.entity.Role;
import com.project.wms.auth.entity.User;
import com.project.wms.auth.exception.InvalidCredentialsException;
import com.project.wms.auth.exception.RegistrationClosedException;
import com.project.wms.auth.repository.RoleRepository;
import com.project.wms.auth.repository.UserRepository;
import com.project.wms.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public TokenPairResponse register(RegisterRequestDto request) {
        if (userRepository.count() > 0) {
            throw new RegistrationClosedException(
                    "Self-registration is closed. Contact an administrator to create an account.");
        }

        User admin = new User();
        admin.setEmail(request.email());
        admin.setPasswordHash(passwordEncoder.encode(request.password()));
        admin.setName(request.name());

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    return roleRepository.save(role);
                });
        admin.setRole(adminRole);

        admin = userRepository.save(admin);
        return issueTokenPair(admin);
    }

    @Transactional
    public TokenPairResponse login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        if (!user.isActive()) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return issueTokenPair(user);
    }

    @Transactional
    public TokenPairResponse refresh(String rawRefreshToken) {
        RefreshTokenService.RotationResult result = refreshTokenService.rotate(rawRefreshToken);
        User user = result.user();

        String accessToken = buildAccessToken(user);
        return new TokenPairResponse(accessToken, result.newRawToken());
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        refreshTokenService.revoke(rawRefreshToken);
    }

    private TokenPairResponse issueTokenPair(User user) {
        String accessToken = buildAccessToken(user);
        String refreshToken = refreshTokenService.issue(user);
        return new TokenPairResponse(accessToken, refreshToken);
    }

    private String buildAccessToken(User user) {
        Set<String> permissions = new HashSet<>();
        if (user.getRole() != null) {
            for (Permission permission : user.getRole().getPermissions()) {
                permissions.add(permission.getName());
            }
        }
        return tokenProvider.generateAccessToken(user.getId(), user.getEmail(), permissions);
    }
}