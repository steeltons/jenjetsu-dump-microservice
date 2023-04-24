package org.jenjetsu.com.brt.rest;

import org.jenjetsu.com.brt.logic.*;
import org.jenjetsu.com.core.dto.AbonentDto;
import org.jenjetsu.com.core.dto.CommandDto;
import org.jenjetsu.com.core.service.AbonentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/manager")
public class ManagerRestController {

    private final AbonentService abonentService;
    private final BillingProcess billingProcess;
    private final RestTemplate restTemplate;

    public ManagerRestController(AbonentService abonentService, BillingProcess billingProcess, RestTemplate restTemplate) {
        this.abonentService = abonentService;
        this.billingProcess = billingProcess;
        this.restTemplate = restTemplate;
    }

    @PatchMapping("/changeTariff")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<?> changeTariff(@RequestBody AbonentDto dto) {
        return ResponseEntity.ok(abonentService.changeTariff(dto));
    }

    @PatchMapping("/billing")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<?> startBilling(@RequestBody CommandDto command) throws IOException {
        if(command.message() != null && command.message().equals("run"))
            return ResponseEntity.ok(billingProcess.billAbonents());
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not run command");
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<?> createNewAbonent(@RequestBody AbonentDto abonentDto) {
        abonentService.create(abonentDto);
        return ResponseEntity.ok(abonentDto);
    }

    @PatchMapping("/generate-abonents")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<?> addNewAbonents() {
        AbonentDto[] abonentDtoArray = restTemplate.getForObject("http://CDR/api/v1/generate", AbonentDto[].class);
        abonentService.createAll(Arrays.asList(abonentDtoArray));
        return ResponseEntity.ok().build();
    }

}
