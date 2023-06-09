package org.jenjetsu.com.cdr2.rest.advice;

import org.jenjetsu.com.core.exception.CdrCreateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class MainControllerAdvice {

    @ExceptionHandler(value = CdrCreateException.class)
    public ResponseEntity<?> handleIOException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Impossible to create cdr file");
    }
}
