package org.jenjetsu.com.hrs.rest.advice;

import org.jenjetsu.com.core.exception.BillFileCreateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BillingRestControllerAdvice {

    @ExceptionHandler(value = BillFileCreateException.class)
    public ResponseEntity<?> handleBillFileCreateException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Impossible to create bill file");
    }
}
