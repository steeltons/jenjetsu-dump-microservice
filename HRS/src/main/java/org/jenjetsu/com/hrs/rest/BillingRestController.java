package org.jenjetsu.com.hrs.rest;

import org.jenjetsu.com.hrs.logic.BillFileManipulator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/billing")
@Deprecated(forRemoval = true)
public class BillingRestController {

    private final BillFileManipulator manipulator;

    public BillingRestController(BillFileManipulator manipulator) {
        this.manipulator = manipulator;
    }

    /**
     * <h2>Bill phone numbers</h2>
     * Accept GET requests to bill phone numbers.
     * @param file - cdr+ binary file
     * @return ResponseEntity with bill binary file
     * @deprecated - bad implementation because length of HttpRequest with binary file can be more than 12MB. Also
     * file cannot be handle and process because serer is shut down or on restart. So now all commands for parseing cdr+
     * file are process through the RabbitMq broker manager.
     */
    @PostMapping("/bill-number")
    @Deprecated(forRemoval = true)
    public ResponseEntity<?> billPhoneNumber(@RequestParam("file")MultipartFile file) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Now RabbitMq accept all incoming requests " +
                "to parse cdr+ file");
    }
}
