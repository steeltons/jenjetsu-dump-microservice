package org.jenjetsu.com.brt.logic;

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
public class CdrFileSender {

    private final String HRS_URL = "http://HRS/api/v1/billing/bill-number";

    private final RestTemplate restTemplate;

    public CdrFileSender(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * <h2>Get bill file from HRS</h2>
     * Send post request to HRS and get bill file
     * @param resource
     * @return
     */
    public Resource getBillFileFromHRS(Resource resource) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", (ByteArrayResource) resource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Resource> response = restTemplate.postForEntity(HRS_URL, requestEntity, Resource.class);
        return response.getBody();
    }
}
