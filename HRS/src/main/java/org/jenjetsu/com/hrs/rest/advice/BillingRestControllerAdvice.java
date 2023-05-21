package org.jenjetsu.com.hrs.rest.advice;

import org.jenjetsu.com.core.exception.BillFileCreateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <h2>handle bill file create exception</h2>
 * @deprecated - handle exceptions from only one controller - BillingRestController. So it will be removed soon as
 * unnecessary.
 */
@RestControllerAdvice
@Deprecated(forRemoval = true)
public class BillingRestControllerAdvice {

    @ExceptionHandler(value = BillFileCreateException.class)
    @Deprecated(forRemoval = true)
    public ResponseEntity<?> handleBillFileCreateException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Impossible to create bill file");
    }
}
