package com.project.wms.auth.service;

import com.project.wms.auth.entity.User;
import com.project.wms.auth.exception.UserNotFoundException;
import com.project.wms.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Enforces the warehouse scope stored on warehouse users. */
@Service
@RequiredArgsConstructor
public class WarehouseAccessService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public void assertCanAccess(Long userId, Long warehouseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        String roleName = user.getRole() != null ? user.getRole().getName() : null;
        if ("ADMIN".equals(roleName)) {
            return;
        }

        if (("WAREHOUSE_MANAGER".equals(roleName) || "WAREHOUSE_STAFF".equals(roleName))
                && user.getWarehouse() != null
                && user.getWarehouse().getId().equals(warehouseId)) {
            return;
        }

        throw new AccessDeniedException("You do not have access to warehouse " + warehouseId);
    }
}
