package org.jenjetsu.com.core.service;

/**
 * <h2>Jwt parser interface</h2>
 * Light version of parser that only can validate and parse access token
 */
public interface JwtParser {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public boolean validateAccessToken(String accessToken);

    /**
     * <h2>Get username from access token</h2>
     * Return phone number or manager login from access token
     * @param accessToken
     * @return Phone number or manager login
     */
    public String getUsernameFromAccessToken(String accessToken);
    public String getRoleFromAccessToken(String accessToken);
}
