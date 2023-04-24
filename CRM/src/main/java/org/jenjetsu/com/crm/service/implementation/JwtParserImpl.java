package org.jenjetsu.com.crm.service.implementation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.crm.service.JwtParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;


@Service
@Slf4j
/**
 * <h2>Jwt Parser</h2>
 * Created to validate tokens and create them
 */
public class JwtParserImpl implements JwtParser {

    private final String secret;
    private final long accessTokenExpriationMillis;
    private final long refreshTokenExpirationMillis;
    private final Algorithm accessTokenAlgorithm;
    private final Algorithm refreshTokenAlgorithm;
    private final JWTVerifier accessTokenVerifier;
    private final JWTVerifier refreshTokenVerifier;
    private final String ISSURER = "jenjetsu";

    public JwtParserImpl(@Value("${secret}") String secret,
                         @Value("${accessTokenExpirationMinutes}") long accessMinutes,
                         @Value("${refreshTokenExpirationDays}") long refreshDays) {
        this.secret = Base64.getEncoder().encodeToString(secret.getBytes(StandardCharsets.UTF_8));
        accessTokenExpriationMillis = accessMinutes * 60 * 1000;
        refreshTokenExpirationMillis = refreshDays * 24 * 60 * 60 * 1000;
        accessTokenAlgorithm = Algorithm.HMAC512(this.secret);
        refreshTokenAlgorithm = Algorithm.HMAC512(this.secret);
        accessTokenVerifier = JWT.require(accessTokenAlgorithm)
                .withIssuer(ISSURER)
                .build();
        refreshTokenVerifier = JWT.require(refreshTokenAlgorithm)
                .withIssuer(ISSURER)
                .build();
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        Date now = new Date(System.currentTimeMillis());
        Date expr = new Date(System.currentTimeMillis() + accessTokenExpriationMillis);
        String role = userDetails.getAuthorities().stream().findFirst().get().toString();
        return JWT.create()
                .withIssuer(ISSURER)
                .withSubject(userDetails.getUsername())
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(expr)
                .sign(accessTokenAlgorithm);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails, String accessTokenId) {
        Date now = new Date(System.currentTimeMillis());
        Date expr = new Date(System.currentTimeMillis() + refreshTokenExpirationMillis);
        String role = userDetails.getAuthorities().stream().findFirst().get().toString();
        return JWT.create()
                .withIssuer(ISSURER)
                .withSubject(userDetails.getUsername())
                .withClaim("tokenId", accessTokenId)
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(expr)
                .sign(refreshTokenAlgorithm);
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        return decodeAccessToken(accessToken).isPresent();
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        return decodeRefreshToken(refreshToken).isPresent();
    }

    /**
     * <h2>Get username from access token</h2>
     * Parse access token and token and returns phone number or username
     * @param accessToken
     * @return phone number in string or username
     */
    @Override
    public String getUsernameFromAccessToken(String accessToken) {
        return decodeAccessToken(accessToken).get().getSubject();
    }

    /**
     * <h2>Get username from refresh token</h2>
     * Parse refresh token and returns phone number or username
     * @param refreshToken
     * @return phone number in string or username
     */
    @Override
    public String getUsernameFromRefreshToken(String refreshToken) {
        return decodeRefreshToken(refreshToken).get().getSubject();
    }

    /**
     * <h2>Get token id from refresh token</h2>
     * Parse refresh token and return refresh token id
     * @param token
     * @return refresh token id
     */
    @Override
    public String getTokenIdFromRefreshToken(String token) {
        return decodeRefreshToken(token).get().getClaim("tokenId").asString();
    }

    /**
     * <h2>Get Role From Refresh Token</h2>
     * Parse refresh token and returns role MANAGER or ABONENT
     * @param token
     * @return MANAGER or ABONENT
     */
    @Override
    public String getRoleFromAccessToken(String token) {
        return decodeAccessToken(token).get().getClaim("role").asString().replace("ROLE_","");
    }

    /**
     * <h2>Get Role From Refresh Token</h2>
     * Parse refresh token and returns role MANAGER or ABONENT
     * @param token
     * @return MANAGER or ABONENT
     */
    @Override
    public String getRoleFromRefreshToken(String token) {
        return decodeRefreshToken(token).get().getClaim("role").asString().replace("ROLE_","");
    }
    private Optional<DecodedJWT> decodeAccessToken(String token){
        try {
            return Optional.of(accessTokenVerifier.verify(token));
        } catch(JWTVerificationException e) {
        }
        return Optional.empty();
    }

    private Optional<DecodedJWT> decodeRefreshToken(String token){
        try {
            return Optional.of(refreshTokenVerifier.verify(token));
        } catch(JWTVerificationException e) {
        }
        return Optional.empty();
    }
}
