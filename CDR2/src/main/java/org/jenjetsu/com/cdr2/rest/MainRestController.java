package org.jenjetsu.com.cdr2.rest;

import org.jenjetsu.com.cdr2.logic.AbonentGenerator;
import org.jenjetsu.com.cdr2.logic.CdrCreator;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h2>Main Rest Controller</h2>
 * Created for two purposes - get cdr file with calls and get phone numbers with moneys
 */
@RestController
@RequestMapping("/api/v1")
public class MainRestController {

    private final AbonentGenerator abonentGenerator;
    private final CdrCreator cdrCreator;

    public MainRestController(AbonentGenerator abonentGenerator, CdrCreator cdrCreator) {
        this.abonentGenerator = abonentGenerator;
        this.cdrCreator = cdrCreator;
    }

    /**
     * <h2>Get calls</h2>
     * Create Cdr file with calls and return it
     * @return ByteArrayResource - file with calls
     */
    @GetMapping("/get-calls")
    public ResponseEntity<?> getCalls() {
        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(cdrCreator.createCdrFile());
    }

    @GetMapping("/generate")
    public ResponseEntity<?> generateAbonents() {
        return ResponseEntity.ok(abonentGenerator.generateAbonents());
    }
}
