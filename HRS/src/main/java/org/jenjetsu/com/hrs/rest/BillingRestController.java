package org.jenjetsu.com.hrs.rest;

import org.jenjetsu.com.hrs.logic.BillFileCreator;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/billing")
public class BillingRestController {

    private final BillFileCreator manipulator;

    public BillingRestController(BillFileCreator manipulator) {
        this.manipulator = manipulator;
    }

    @PostMapping("/bill-number")
    public ResponseEntity<?> billPhoneNumber(@RequestParam("file")MultipartFile file) {
        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(manipulator.createBillFile(file));
    }
}
