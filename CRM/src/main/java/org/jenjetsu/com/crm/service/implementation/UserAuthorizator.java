package org.jenjetsu.com.crm.service.implementation;

import lombok.SneakyThrows;
import org.jenjetsu.com.core.dto.TokenDto;
import org.jenjetsu.com.core.dto.UserDto;
import org.jenjetsu.com.core.entity.RefreshToken;
import org.jenjetsu.com.core.service.RefreshTokenService;
import org.jenjetsu.com.crm.exception.PasswordEqualException;
import org.jenjetsu.com.crm.service.JwtParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * <h2>User authenticator service</h2>
 * Created to User authorization and deauthorization.
 */
@Service
public class UserAuthorizator {

    private final UserDetailsService managerService;
    private final UserDetailsService abonentService;
    private final JwtParser jwtParser;
    private final RefreshTokenService tokenService;

    /**
     * <h2>Constructof for class</h2>
     * @param managerService - in memory user database
     * @param abonentService - abonent phone database
     * @param jwtParser - parser access and refresh token
     * @param tokenService -
     */
    public UserAuthorizator(@Qualifier("managerService") UserDetailsService managerService,
                            @Qualifier("abonentService") UserDetailsService abonentService,
                            JwtParser jwtParser,
                            RefreshTokenService tokenService) {
        this.managerService = managerService;
        this.abonentService = abonentService;
        this.jwtParser = jwtParser;
        this.tokenService = tokenService;
    }

    /**
     * <h2>Authorize Abonent</h2>
     * @param dto - contains phone number and password
     * @return TokenDto - refresh token, access token and adonent phone number
     */
    @SneakyThrows
    public TokenDto authorizeAbonent(UserDto dto) {
        UserDetails details = abonentService.loadUserByUsername(dto.username());
        if(!details.getPassword().equals(dto.password())) {
            throw new PasswordEqualException(String.format("Invalid password for abonent %s", dto.username()));
        }
        RefreshToken token = createRefreshTokenFromUserDetails(details);
        String accessToken = jwtParser.generateAccessToken(details);
        String refreshToken = jwtParser.generateRefreshToken(details, token.getId());
        return new TokenDto(details.getUsername(), accessToken, refreshToken);
    }

    /**
     * <h2>Authorize Manager</h2>
     * @param dto - contains manager login and password
     * @return TokenDto - refresh token, access token and adonent phone number
     */
    @SneakyThrows
    public TokenDto authorizeManager(UserDto dto) {
        UserDetails details = managerService.loadUserByUsername(dto.username());
        if(!details.getPassword().equals(dto.password())) {
            throw new PasswordEqualException(String.format("Invalid password for manager %s", dto.username()));
        }
        RefreshToken token = createRefreshTokenFromUserDetails(details);
        String accessToken = jwtParser.generateAccessToken(details);
        String refreshToken = jwtParser.generateRefreshToken(details, token.getId());
        return new TokenDto(details.getUsername(), accessToken, refreshToken);
    }

    /**
     * <h2>Logout method</h2>
     * @param dto - token dto, that must contains refresh token
     */
    public void logout(TokenDto dto) {
        String refreshToken = dto.refreshToken();
        if(!jwtParser.validateRefreshToken(refreshToken)
                || !tokenService.existById(jwtParser.getTokenIdFromRefreshToken(refreshToken))) {
            throw new BadCredentialsException("Refresh token is not valid");
        }
        tokenService.deleteByOwnerId(jwtParser.getUsernameFromRefreshToken(refreshToken));
    }

    private RefreshToken createRefreshTokenFromUserDetails(UserDetails details) {
        RefreshToken token = new RefreshToken();
        token.setOwnerId(details.getUsername());
        tokenService.create(token);
        return token;
    }
}
