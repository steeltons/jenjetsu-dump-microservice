package org.jenjetsu.com.brt.logic;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.exception.CdrPlusCreateException;
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
@Slf4j
public class PhoneCallGrouper {

    private final AbonentService abonentService;

    public PhoneCallGrouper(AbonentService abonentService) {
        this.abonentService = abonentService;
    }

    /**
     * <h2>Group phone Calls By Phone</h2>
     * Method for group phone calls by phone numbers
     * @param resource - byte file
     * @return Map that grouped by phone numbers
     */
    public Map<Long, List<String>> groupPhoneCallsByPhone(Resource resource) {
        Map<Long, List<String>> phoneCallsMap = new HashMap<>();
        try(Scanner scanner = new Scanner(resource.getInputStream())) {
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
        } catch (Exception e) {
            log.error("PhoneCallGrouper: error parse file. Message: {}", e.getMessage());
            throw new CdrPlusCreateException(String.format("Impossible to create cdr+ file. Error message: %s", e.getMessage()));
        }
        return phoneCallsMap;
    }

    private Long getPhoneNumberFromLine(String line) {
        int firstCommaPos = line.indexOf(",") + 1;
        String stringPhoneNumber = (line.substring(firstCommaPos, line.indexOf(",", firstCommaPos)).trim());
        return Long.parseLong(stringPhoneNumber);
    }

}
