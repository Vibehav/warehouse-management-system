package com.project.wms.auth.dto;

public record UserResponseDto(Long id, String email, String name, String roleName, boolean active) {
}
