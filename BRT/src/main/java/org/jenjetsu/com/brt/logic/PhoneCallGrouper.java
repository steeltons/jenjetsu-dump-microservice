package org.jenjetsu.com.brt.logic;

import lombok.SneakyThrows;
import org.jenjetsu.com.core.service.AbonentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <h2>Phone call grouper</h2>
 * Class which groups calls by phone numbers
 */
@Service
public class PhoneCallGrouper {

    private final AbonentService abonentService;

    public PhoneCallGrouper(AbonentService abonentService) {
        this.abonentService = abonentService;
    }

    /**
     * <h2>Group phone Calls By Phone</h2>
     * Method for group phone calls by phone numbers
     * @param cdr - file resource
     * @return Map that grouped by phone numbers
     */
    @SneakyThrows
    public Map<Long, List<String>> groupPhoneCallsByPhone(Resource cdr) {
        Map<Long, List<String>> phoneCallsMap = new HashMap<>();
        Scanner scanner = new Scanner(cdr.getInputStream());
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            Long phoneNumber = getPhoneNumberFromLine(line);
            if (!abonentService.isExistByPhoneNumber(phoneNumber)) {
                continue;
            }
            if (!phoneCallsMap.containsKey(phoneNumber)) {
                phoneCallsMap.put(phoneNumber, new ArrayList<>());
            }
            phoneCallsMap.get(phoneNumber).add(line);
        }
        return phoneCallsMap;
    }

    private Long getPhoneNumberFromLine(String line) {
        int firstCommaPos = line.indexOf(",") + 1;
        String stringPhoneNumber = (line.substring(firstCommaPos, line.indexOf(",", firstCommaPos)).trim());
        return Long.parseLong(stringPhoneNumber);
    }

}
