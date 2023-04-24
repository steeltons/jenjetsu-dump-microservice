package org.jenjetsu.com.crm.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtParser {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public String generateAccessToken(UserDetails userDetails);
    public String generateRefreshToken(UserDetails userDetails, String refreshTokenId);
    public boolean validateAccessToken(String accessToken);
    public boolean validateRefreshToken(String refreshToken);
    public String getUsernameFromAccessToken(String accessToken);
    public String getUsernameFromRefreshToken(String accessToken);

    public String getTokenIdFromRefreshToken(String token);
    public String getRoleFromAccessToken(String accessToken);
    public String getRoleFromRefreshToken(String refreshToken);
}
