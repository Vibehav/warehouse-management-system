package com.project.wms.auth.dto;


/** What actually goes in the JSON response body — the refresh token never
 * appears here, only in the HttpOnly cookie. */
public record AccessTokenResponseDto(String accessToken) {}
