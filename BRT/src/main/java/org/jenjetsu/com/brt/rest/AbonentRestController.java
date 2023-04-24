package org.jenjetsu.com.brt.rest;

import org.jenjetsu.com.core.dto.AbonentDto;
import org.jenjetsu.com.core.service.AbonentPayloadService;
import org.jenjetsu.com.core.service.AbonentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/abonent")
public class AbonentRestController {

    private final AbonentService abonentService;

    public AbonentRestController(AbonentService abonentService) {
        this.abonentService = abonentService;
    }

    @PatchMapping("/pay")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER','ROLE_ABONENT')")
    public ResponseEntity<?> pay(@RequestBody AbonentDto dto) {
        return ResponseEntity.ok(abonentService.addMoney(dto));
    }

    @GetMapping("/report/{numberPhone}")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER','ROLE_ABONENT')")
    public ResponseEntity<?> getReports(@PathVariable("numberPhone") Long numberPhone) {
        return ResponseEntity.ok(abonentService.getMyPayloads(numberPhone));
    }

}
