package org.jenjetsu.com.brt.logic;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * <h2>Cdr file getter</h2>
 * Class that send get request to CDR to get infotrmation of calls
 */
@Service
public class CdrFileGetter {

    private final String CDR_URL = "http://CDR/api/v1/get-calls";
    private final RestTemplate restTemplate;

    public CdrFileGetter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * <h2>Get CDR file</h2>
     * Method that get call information file from CDR
     * @return ByteArrayResources - byte file
     * @throws IOException
     */
    public Resource getCdrFile() throws IOException {
        return restTemplate.getForObject(CDR_URL, Resource.class);
    }
}
