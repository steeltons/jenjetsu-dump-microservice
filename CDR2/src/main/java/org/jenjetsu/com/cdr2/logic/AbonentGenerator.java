package org.jenjetsu.com.cdr2.logic;

import org.jenjetsu.com.core.dto.AbonentDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * <h2>Abonent generator</h2>
 */
@Service
public class AbonentGenerator {

    private final PhoneNumberCreator phoneNumberCreator;
    private final String[] tariffs = {"03", "06", "11"};

    public AbonentGenerator(PhoneNumberCreator phoneNumberCreator) {
        this.phoneNumberCreator = phoneNumberCreator;
    }

    public List<AbonentDto> generateAbonents() {
        Set<Long> phones = phoneNumberCreator.createSetOfPhoneNumbers();
        List<AbonentDto> dtos = new ArrayList<>();
        Random random = new Random();
        for(Long phone :phones) {
            double sum = Math.round(random.nextDouble(-100, 1000) * 100) / 100.0;
            String tariff = tariffs[random.nextInt(0,3)];
            AbonentDto dto = new AbonentDto(phone, tariff, sum);
            dtos.add(dto);
        }
        return dtos;
    }
}
