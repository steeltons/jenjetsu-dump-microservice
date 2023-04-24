package org.jenjetsu.com.crm.service.implementation;

import org.jenjetsu.com.core.dto.TokenDto;
import org.jenjetsu.com.core.entity.RefreshToken;
import org.jenjetsu.com.core.entity.Role;
import org.jenjetsu.com.core.service.RefreshTokenService;
import org.jenjetsu.com.crm.service.JwtParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * <h2>Token manipulator</h2>
 * Created to manipulate with token.
 */
@Service
public class TokenManipulator {

    private final JwtParser jwtParser;
    private final RefreshTokenService tokenService;
    private final UserDetailsService managerService;
    private final UserDetailsService abonentService;

    public TokenManipulator(@Qualifier("managerService") UserDetailsService managerService,
                            @Qualifier("abonentService") UserDetailsService abonentService,
                            JwtParser jwtParser,
                            RefreshTokenService tokenService) {
        this.jwtParser = jwtParser;
        this.tokenService = tokenService;
        this.abonentService = abonentService;
        this.managerService = managerService;
    }

    /**
     * <h2>Refresh token</h2>
     * Create refresh and access token from refresh token
     * @param dto - must contains refresh token
     * @return tokenDto - refresh token, access token and username (phone number or username)
     */
    public TokenDto refreshToken(TokenDto dto) {
        String refreshToken = dto.refreshToken();
        if(!jwtParser.validateRefreshToken(refreshToken)
                || !tokenService.existById(jwtParser.getTokenIdFromRefreshToken(refreshToken))) {
            throw new BadCredentialsException("Refresh token is not valid");
        }
        RefreshToken token = new RefreshToken();
        UserDetails details = null;
        if(jwtParser.getRoleFromRefreshToken(refreshToken).equals(Role.MANAGER)) {
            details = managerService.loadUserByUsername(jwtParser.getUsernameFromRefreshToken(refreshToken));
        } else {
            details = abonentService.loadUserByUsername(jwtParser.getUsernameFromRefreshToken(refreshToken));
        }
        token.setOwnerId(details.getUsername());
        tokenService.create(token);
        String accessToken = jwtParser.generateAccessToken(details);
        refreshToken = jwtParser.generateRefreshToken(details, token.getId());
        return new TokenDto(details.getUsername(), accessToken, refreshToken);
    }


}
