package org.jenjetsu.com.core.dto;

public record TokenDto(String username,
                       String accessToken,
                       String refreshToken) {
}
