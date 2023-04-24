package org.jenjetsu.com.crm.rest.advice;

import org.jenjetsu.com.crm.exception.PasswordEqualException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthAdvice {

    @ExceptionHandler(value = {PasswordEqualException.class, UsernameNotFoundException.class})
    public ResponseEntity<?> handleAuthorizationException(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }
}


