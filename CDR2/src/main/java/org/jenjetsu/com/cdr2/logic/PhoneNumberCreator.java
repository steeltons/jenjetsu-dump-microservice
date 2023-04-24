package org.jenjetsu.com.cdr2.logic;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * <h2>Phone number creator</h2>
 * Class that create list of phone numbers
 */
@Service
@Scope("prototype")
public class PhoneNumberCreator {

    private final String NUMBER_PATTERN;
    private final int MAX_PHONE_NUMBERS;
    private final int MIN_CALL_NUMBERS;
    private final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("[1-9][0-9#]{10}");

    public PhoneNumberCreator(@Value("${PhoneNumberCreator.phone-pattern}") String number_pattern,
                              @Value("${PhoneNumberCreator.max-call-numbers}") Integer max_phone_numbers,
                              @Value("${PhoneNumberCreator.min-call-numbers}")Integer min_call_numbers) {
        if(min_call_numbers < 0 || max_phone_numbers < 0) {
            throw new IllegalArgumentException("Call numbers must be bigger than 0");
        }
        if(min_call_numbers > max_phone_numbers) {
            throw new IllegalArgumentException("Min call numbers are bigger than max call");
        }
        if(!PHONE_NUMBER_PATTERN.matcher(number_pattern).find()){
            throw new IllegalArgumentException("Phone number does not match pattern");
        }
        NUMBER_PATTERN = number_pattern;
        MAX_PHONE_NUMBERS = max_phone_numbers;
        MIN_CALL_NUMBERS = min_call_numbers;
    }

    /**
     * <h2>Create list of phone numbers</h2>
     * Method that generate phone numbers
     * @return Set of phone numbers
     */
    public Set<Long> createSetOfPhoneNumbers() {
        Set<Long> phoneNumbers = new HashSet<>();
        Faker faker = new Faker();
        int callNumbers = new Random().nextInt(MIN_CALL_NUMBERS, MAX_PHONE_NUMBERS);
        while (phoneNumbers.size() < MAX_PHONE_NUMBERS) {
            phoneNumbers.add(Long.parseLong(faker.numerify(NUMBER_PATTERN)));
        }
        phoneNumbers.add(89146878167l);
        return phoneNumbers;
    }
}
