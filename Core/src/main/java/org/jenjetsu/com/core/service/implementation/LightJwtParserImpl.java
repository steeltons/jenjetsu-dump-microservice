package org.jenjetsu.com.core.service.implementation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.jenjetsu.com.core.service.JwtParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Service(value = "lightJwtParser")
public class LightJwtParserImpl implements JwtParser {

    private final String secret;
    private final Algorithm accessTokenAlgorithm;
    private final JWTVerifier accessTokenVerifier;
    private final String ISSURER = "jenjetsu";


    public LightJwtParserImpl(@Value("12345") String secret) {
        this.secret = Base64.getEncoder().encodeToString(secret.getBytes(StandardCharsets.UTF_8));
        accessTokenAlgorithm = Algorithm.HMAC512(this.secret);
        accessTokenVerifier = JWT.require(accessTokenAlgorithm)
                .withIssuer(ISSURER)
                .build();
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        return decodeAccessToken(accessToken).isPresent();
    }


    @Override
    public String getUsernameFromAccessToken(String accessToken) {
        return decodeAccessToken(accessToken).get().getSubject();
    }

    @Override
    public String getRoleFromAccessToken(String token) {
        return decodeAccessToken(token).get().getClaim("role").asString().replace("ROLE_","");
    }

    private Optional<DecodedJWT> decodeAccessToken(String token){
        try {
            return Optional.of(accessTokenVerifier.verify(token));
        } catch(JWTVerificationException e) {
        }
        return Optional.empty();
    }
}
