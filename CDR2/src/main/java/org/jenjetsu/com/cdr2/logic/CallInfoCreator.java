package org.jenjetsu.com.cdr2.logic;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.jenjetsu.com.core.entity.CallInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <h2>Call Info Creator</h2>
 *
 */
@Service
@Scope("prototype")
public class CallInfoCreator {

    private final Long MIN_CALLING_DURATION_SECONDS;
    private final Long MAX_CALLING_DURATION_SECONDS;
    private final Date MIN_CALLING_DATE;
    private final Date MAX_CALLING_DATE;
    private final Integer MIN_PHONE_CALLS;
    private final Integer MAX_PHONE_CALLS;

    public CallInfoCreator(@Value("${CallInfoCreator.min-call-duration-seconds}") Long min_duration,
                           @Value("${CallInfoCreator.max-call-duration-seconds}") Long max_duration,
                           @Value("${CallInfoCreator.min-call-date}") String min_calling_date,
                           @Value("${CallInfoCreator.max-call-date}") String max_calling_date,
                           @Value("${CallInfoCreator.min-phone-calls}") Integer min_phone_calls,
                           @Value("${CallInfoCreator.max-phone-calls}") Integer max_phone_calls) {
        if(min_duration > max_duration || min_duration < 0 || max_duration < 0) {
            throw new IllegalArgumentException("Invalid calling duration");
        }
        if(min_phone_calls > max_phone_calls || min_phone_calls < 0 || max_phone_calls < 0) {
            throw new IllegalArgumentException("Invalid amount of phone calls");
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            MIN_CALLING_DATE = format.parse(min_calling_date);
            MAX_CALLING_DATE = format.parse(max_calling_date);
        } catch (ParseException e) {
            throw new RuntimeException("Not correct format for date. Date format must be yyyy-MM-dd");
        }
        if(MIN_CALLING_DATE.after(MAX_CALLING_DATE)) {
            throw new IllegalArgumentException("Min calling date is after max");
        }
        MIN_CALLING_DURATION_SECONDS = min_duration;
        MAX_CALLING_DURATION_SECONDS = max_duration;
        MIN_PHONE_CALLS = min_phone_calls;
        MAX_PHONE_CALLS = max_phone_calls;
    }

    /**
     * <h2>Create Set of CallInformation</h2>
     * Method that create calls for abonents
     * @param phoneNumbers - Collection of phone Number
     * @return Set of calls for phone numbers
     */
    @SneakyThrows
    public Set<CallInformation> createSetOfCallInformation(Collection<Long> phoneNumbers) {
        Faker faker = new Faker();
        Random random = new Random();
        Set<CallInformation> output = new HashSet<>();
        for(Long number : phoneNumbers) {
            int randCalls = random.nextInt(MIN_PHONE_CALLS, MAX_PHONE_CALLS);
            Set<CallInformation> calls = new HashSet<>();
            while (calls.size() < randCalls) {
                long duration = randomCallingDurationInSeconds();
                Date startCallingDate = faker.date().between(MIN_CALLING_DATE, MAX_CALLING_DATE);
                Date endCallingDate = new Date(startCallingDate.getTime() + duration * 1000);
                int callType = randomCallType();
                CallInformation callInformation = new CallInformation();
                callInformation.setPhoneNumber(number);
                callInformation.setCallType((byte) callType);
                callInformation.setStartCallingTime(startCallingDate);
                callInformation.setEndCallingTime(endCallingDate);
                calls.add(callInformation);
            }
            output.addAll(calls);
        }
        return output;
    }
    private long randomCallingDurationInSeconds() {
        Random random = new Random();
        return random.nextLong(MIN_CALLING_DURATION_SECONDS, MAX_CALLING_DURATION_SECONDS);
    }

    private byte randomCallType() {
        Random random = new Random();
        return (byte) random.nextInt(1, 3);
    }
}
