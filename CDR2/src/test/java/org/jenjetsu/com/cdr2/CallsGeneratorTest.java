package org.jenjetsu.com.cdr2;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.cdr2.logic.CallInfoCreator;
import org.jenjetsu.com.core.entity.CallInformation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class CallsGeneratorTest {

    @Mock
    private CallInfoCreator callInfoCreator;

    @Test
    public void checkWorkingStatusTest() {
        List<Long> phones = Arrays.asList(11111111111l, 22222222222l, 33333333333l, 44444444444l);
        Collection<CallInformation> calls = callInfoCreator.createSetOfCallInformation(phones);
        calls.forEach(call -> assertTrue(CallInfoValidator.isCallValid(call)));
    }
}
