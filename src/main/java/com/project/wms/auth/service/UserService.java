package com.project.wms.auth.service;


import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.wms.auth.dto.CreateUserRequestDto;
import com.project.wms.auth.dto.UserResponseDto;
import com.project.wms.auth.entity.Role;
import com.project.wms.auth.entity.User;
import com.project.wms.auth.exception.EmailAlreadyRegisteredException;
import com.project.wms.auth.exception.RolesNotFoundException;
import com.project.wms.auth.exception.UserNotFoundException;
import com.project.wms.auth.repository.RefreshTokenRepository;
import com.project.wms.auth.repository.RoleRepository;
import com.project.wms.auth.repository.UserRepository;
import com.project.wms.supplier.domain.Supplier;
import com.project.wms.supplier.service.SupplierService;
import com.project.wms.warehouse.domain.Warehouse;
import com.project.wms.warehouse.exception.WarehouseNotFoundException;
import com.project.wms.warehouse.repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final WarehouseRepository warehouseRepository;
    private final PasswordEncoder passwordEncoder;
    private final SupplierService supplierService;

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

        validateScope(role.getName(), request.warehouseId(), request.supplierId());

        if (request.warehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                    .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found: " + request.warehouseId()));
            user.setWarehouse(warehouse);
        }

        if (request.supplierId() != null) {
            Supplier supplier = supplierService.getByActiveId(request.supplierId());
            user.setSupplier(supplier);
        }

        user = userRepository.save(user);
        return responseDto(user);
    }

    @Transactional
    public void softDeleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        Instant now = Instant.now();
        user.setActive(false);
        user.setDeletedAt(now);
        userRepository.save(user);
        refreshTokenRepository.revokeActiveByUserId(userId, now);
    }

    @Transactional
    public void restoreUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        user.setActive(true);
        user.setDeletedAt(null);
        userRepository.save(user);
    }

    private UserResponseDto responseDto(User user) {
        String roleName = user.getRole() != null ? user.getRole().getName() : null;
        return new UserResponseDto(user.getId(), user.getEmail(), user.getName(), roleName, user.isActive());
    }

    private void validateScope(String roleName, Long warehouseId, Long supplierId) {
        switch (roleName) {
            case "ADMIN", "BUSINESS_CUSTOMER" -> requireNoScope(roleName, warehouseId, supplierId);
            case "WAREHOUSE_MANAGER", "WAREHOUSE_STAFF" -> {
                if (warehouseId == null || supplierId != null) {
                    throw new IllegalArgumentException(roleName + " requires a warehouse and cannot have a supplier");
                }
            }
            case "SUPPLIER" -> {
                if (supplierId == null || warehouseId != null) {
                    throw new IllegalArgumentException("SUPPLIER requires a supplier and cannot have a warehouse");
                }
            }
            default -> throw new IllegalArgumentException("Unsupported role: " + roleName);
        }
    }

    private void requireNoScope(String roleName, Long warehouseId, Long supplierId) {
        if (warehouseId != null || supplierId != null) {
            throw new IllegalArgumentException(roleName + " cannot have a warehouse or supplier scope");
        }
    }
}
