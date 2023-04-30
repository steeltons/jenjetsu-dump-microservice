package org.jenjetsu.com.brt.logic;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.exception.BillReadFileException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * <h2>Cdr file sender</h2>
 * Class that send post request to HRS to bill phone calls of abonent
 */
@Service
@Slf4j
public class CdrFileSender {

    private final String HRS_URL = "http://HRS/api/v1/billing/bill-number";

    private final RestTemplate restTemplate;

    public CdrFileSender(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * <h2>Get bill file from HRS</h2>
     * Send post request to HRS and get bill file path
     * @param cdrPlusFile - cdr+ byte file
     * @return billFileResource - bill byte file
     */
    public Resource getBillFileFromHRS(Resource cdrPlusFile) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", cdrPlusFile);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            return restTemplate.postForObject(HRS_URL, requestEntity, Resource.class);
        } catch (Exception e) {
            log.error("CdrFileSender: ERROR to get file path. Error message: {}", e.getMessage());
            throw new BillReadFileException(String.format("Impossible to read bill file. error message %s", e.getMessage()));
        }
    }
}
