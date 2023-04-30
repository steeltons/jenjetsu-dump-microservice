package org.jenjetsu.com.brt.logic;

import org.jenjetsu.com.core.dto.CdrDto;
import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.service.AbonentService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <h2>CdrDto converter</h2>
 * Class which convert map of calls grouped by phone numbers to list of cdrDto
 */
@Service
public class CdrDtoConverter {

    private final AbonentService abonentService;

    public CdrDtoConverter(AbonentService abonentService) {
        this.abonentService = abonentService;
    }

    /**
     * <h2>Convert calls map to cdrs</h2>
     * Method whic convert map of calls grouped by phone numbers to cdr list
     * @param phoneCallsMap
     * @return list of cdr dto
     */
    public List<CdrDto> convertCallsMapToCdrs(Map<Long, List<String>> phoneCallsMap) {
        List<CdrDto> cdrDtos = new ArrayList<>();
        for(Map.Entry<Long, List<String>> phoneCalls : phoneCallsMap.entrySet()) {
            Abonent abonent = abonentService.findByPhoneNumber(phoneCalls.getKey());
            Long phoneNumber = phoneCalls.getKey();
            String tariffId = abonent.getTariff().getId();
            List<String> calls = phoneCalls.getValue();
            CdrDto dto = new CdrDto(phoneNumber, tariffId, calls);
            cdrDtos.add(dto);
        }
        return cdrDtos;
    }
}
