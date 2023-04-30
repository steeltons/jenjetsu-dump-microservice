package org.jenjetsu.com.brt.logic;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.dto.PhoneNumbersDto;
import org.jenjetsu.com.core.exception.CdrPlusCreateException;
import org.jenjetsu.com.core.service.AbonentService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <h2>Cdr file getter</h2>
 * Class that send get request to CDR to get infotrmation of calls
 */
@Service
@Slf4j
public class CdrFileGetter {

    private final String CDR_URL = "http://CDR/api/v1/generate-calls";
    private final RestTemplate restTemplate;
    private final AbonentService abonentService;

    public CdrFileGetter(RestTemplate restTemplate,
                         AbonentService abonentService) {
        this.restTemplate = restTemplate;
        this.abonentService = abonentService;
    }

    /**
     * <h2>Get CDR file</h2>
     * Method that get call information file from CDR
     * @return resourse - byte file
     * @throws IOException
     */
    public Resource getCdrFilePath() {
        Resource resource = null;
        try {
            Collection<Long> phones = abonentService.findAll().stream()
                    .map(abonent -> abonent.getPhoneNumber()).collect(Collectors.toList());
            resource = restTemplate.postForObject(CDR_URL, new PhoneNumbersDto(phones), Resource.class);
        } catch (Exception e) {
            log.error("CdrFileGetter: ERROR to get file path. Error message: {}", e.getMessage());
            throw new CdrPlusCreateException(String.format("Impossible to create cdr+ file. Error message: %s", e.getMessage()));
        }
        return resource;
    }
}
