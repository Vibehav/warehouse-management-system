package com.project.wms.auth.dto;

public record CreateUserRequestDto(String email,
                                   String password,
                                   String name,
                                   Long roleId,
                                   Long warehouseId ) {
}
