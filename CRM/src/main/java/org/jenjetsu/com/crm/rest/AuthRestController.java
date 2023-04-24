package org.jenjetsu.com.crm.rest;


import org.jenjetsu.com.core.dto.TokenDto;
import org.jenjetsu.com.core.dto.UserDto;
import org.jenjetsu.com.crm.service.implementation.TokenManipulator;
import org.jenjetsu.com.crm.service.implementation.UserAuthorizator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h2>Authentication rest controller</h2>
 *
 */
@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthRestController {

    private final UserAuthorizator authenticator;
    private final TokenManipulator tokenManipulator;

    public AuthRestController(UserAuthorizator authenticator, TokenManipulator tokenManipulator) {
        this.authenticator = authenticator;
        this.tokenManipulator = tokenManipulator;
    }

    @PostMapping("/abonent")
    public ResponseEntity<?> authenticateAbonent(@RequestBody UserDto dto) {
        return ResponseEntity.ok(authenticator.authorizeAbonent(dto));
    }

    @PostMapping("/manager")
    public ResponseEntity<?> authenticateManager(@RequestBody UserDto dto) {
        return ResponseEntity.ok(authenticator.authorizeManager(dto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenDto dto) {
        return ResponseEntity.ok(tokenManipulator.refreshToken(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody TokenDto dto) {
        authenticator.logout(dto);
        return ResponseEntity.ok().build();
    }
}
