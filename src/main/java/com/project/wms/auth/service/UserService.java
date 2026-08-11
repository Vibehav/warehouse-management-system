package com.project.wms.auth.service;


import com.project.wms.auth.dto.CreateUserRequestDto;
import com.project.wms.auth.dto.UserResponseDto;
import com.project.wms.auth.entity.Role;
import com.project.wms.auth.entity.User;
import com.project.wms.auth.exception.EmailAlreadyRegisteredException;
import com.project.wms.auth.exception.RolesNotFoundException;
import com.project.wms.auth.exception.UserNotFoundException;
import com.project.wms.auth.repository.RoleRepository;
import com.project.wms.auth.repository.UserRepository;
import com.project.wms.warehouse.domain.Warehouse;
import com.project.wms.warehouse.exception.WarehouseNotFoundException;
import com.project.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final WarehouseRepository warehouseRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto request){
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyRegisteredException("Email already registered: " + request.email());
        }

        Role role = roleRepository.findById(request.roleId()).orElseThrow(()-> new RolesNotFoundException("Role not found."));

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setName(request.name());
        user.setRole(role);

        if (request.warehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                    .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found: " + request.warehouseId()));
            user.setWarehouse(warehouse);
        }

        user = userRepository.save(user);
        return responseDto(user);
    }

    @Transactional
    public void softDeleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        user.setActive(false);
        user.setDeletedAt(Instant.now());
        userRepository.save(user);
        // Note: does NOT revoke this user's currently-valid token — see
        // AuthService.logout() comment for why that's a separate, deferred gap.
    }

    private UserResponseDto responseDto(User user) {
        String roleName = user.getRole() != null ? user.getRole().getName() : null;
        return new UserResponseDto(user.getId(), user.getEmail(), user.getName(), roleName, user.isActive());
    }
}