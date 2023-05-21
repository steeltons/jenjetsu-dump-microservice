package org.jenjetsu.com.cdr2.rest;

import org.jenjetsu.com.cdr2.logic.AbonentGenerator;
import org.jenjetsu.com.cdr2.logic.CdrFileManipulator;
import org.jenjetsu.com.core.dto.PhoneNumbersDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <h2>Main Rest Controller</h2>
 * Created for two purposes - get cdr file with calls and get phone numbers with moneys
 */
@RestController
@RequestMapping("/api/v1")
public class MainRestController {

    private final AbonentGenerator abonentGenerator;
    private final CdrFileManipulator cdrFileManipulator;

    public MainRestController(AbonentGenerator abonentGenerator, CdrFileManipulator cdrFileManipulator) {
        this.abonentGenerator = abonentGenerator;
        this.cdrFileManipulator = cdrFileManipulator;
    }

    /**
     * <h2>Get calls</h2>
     * Create Cdr file with calls and return it
     * @return ByteArrayResource - file with calls
     * @Deprecated - now RabbitMq broker messenger accept all commands.
     */
    @GetMapping("/get-calls")
    @Deprecated(forRemoval = true)
    public ResponseEntity<?> getCalls() {
//        return ResponseEntity.ok()
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .body(cdrCreator.createCdrFile());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @PostMapping("/generate-calls")
    @Deprecated(forRemoval = true)
    public ResponseEntity<?> generateCalls(@RequestBody PhoneNumbersDto phoneNumbersDto) {
        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(cdrFileManipulator.createCdrFileByPhoneNumbers(phoneNumbersDto));
    }

    @GetMapping("/generate")
    public ResponseEntity<?> generateAbonents() {
        return ResponseEntity.ok(abonentGenerator.generateAbonents());
    }
}
