package org.jenjetsu.com.cdr2;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.cdr2.logic.CallInfoCreator;
import org.jenjetsu.com.cdr2.logic.CdrFileResourceCreator;
import org.jenjetsu.com.cdr2.logic.PhoneNumberCreator;
import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.core.util.CallInformationParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class CdrResourceCreatorTest {

    @Spy
    private final CdrFileResourceCreator resourceCreator;
    @Spy
    private final CallInfoCreator callInfoCreator;
    @Spy
    private final PhoneNumberCreator numberCreator;

    public CdrResourceCreatorTest() {
        this.resourceCreator = new CdrFileResourceCreator();
        this.callInfoCreator = new CallInfoCreator(10l, 200l,
                "022-01-01", "2022-12-31", 3, 20);
        this.numberCreator = new PhoneNumberCreator("7##########", 100, 50);
    }

    @Test
    public void checkWorkingStatusTest() {
        Collection<Long> numbers = Arrays.asList(11111111111l, 22222222222l, 33333333333l, 44444444444l);
        Collection<CallInformation> calls = callInfoCreator.createSetOfCallInformation(numbers);
        Resource resource = resourceCreator.createResourceFromCalls(calls);
        boolean result = isResourceReadable(resource);
        assertTrue(result);
    }

    @Test
    public void checkWorkingWithRandom() {
        Collection<Long> numbers = numberCreator.createSetOfPhoneNumbers();
        Collection<CallInformation> calls = callInfoCreator.createSetOfCallInformation(numbers);
        Resource resource = resourceCreator.createResourceFromCalls(calls);
        boolean result = isResourceReadable(resource);
        assertTrue(result);
    }

    @Test
    public void checkThrowError() {
        Collection<CallInformation> calls = null;
        assertThrows(Exception.class, () -> resourceCreator.createResourceFromCalls(calls));
    }

    private boolean isResourceReadable(Resource resource) {
        boolean res = true;
        try(Scanner scanner = new Scanner(resource.getInputStream())) {
            while (scanner.hasNext()) {
                CallInformation call = CallInformationParser.parseCalInformation(scanner.nextLine());
                if(!CallInfoValidator.isCallValid(call)) {
                    throw new Exception(String.format("Call information is not valid: %s", call.toString()));
                }
            }
        } catch (Exception e) {
            log.error("CdrResourceCreatorTest: RESOURCE FILE NOT VALID AND CANNOT BE PARSED. Error message: {}", e.getMessage());
            res = false;
        }
        return res;
    }
}
