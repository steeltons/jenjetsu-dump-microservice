package org.jenjetsu.com.brt.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jenjetsu.com.brt.broker.sender.CdrMessageSender;
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
@AllArgsConstructor
@RequestMapping("/api/v1/manager")
public class ManagerRestController {

    private final AbonentService abonentService;
    private final RestTemplate restTemplate;
    private final BillingProcess billingProcess;

    @PatchMapping("/changeTariff")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<?> changeTariff(@RequestBody AbonentDto dto) {
        return ResponseEntity.ok(abonentService.changeTariff(dto));
    }

    @PatchMapping("/billing")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<?> startBilling(@RequestBody CommandDto command,
                                          HttpServletRequest request,
                                          HttpServletResponse response){
        if(command.message() != null && command.message().equals("run")) {
            billingProcess.startBilling(request, response);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not run command");
        }
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
