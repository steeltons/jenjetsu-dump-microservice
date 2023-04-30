package org.jenjetsu.com.cdr2.logic;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.dto.AbonentDto;
import org.jenjetsu.com.core.dto.PhoneNumbersDto;
import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.entity.CallInformation;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
public class CdrCreator {

    private final CdrFileWriter cdrFileWriter;
    private final PhoneNumberCreator phoneNumberCreator;
    private final CallInfoCreator callInfoCreator;
    private final CdrFileResourceCreator resourceCreator;
    public CdrCreator(CdrFileWriter cdrFileWriter,
                      PhoneNumberCreator phoneNumberCreator,
                      CallInfoCreator callInfoCreator,
                      CdrFileResourceCreator resourceCreator) {
        this.cdrFileWriter = cdrFileWriter;
        this.phoneNumberCreator = phoneNumberCreator;
        this.callInfoCreator = callInfoCreator;
        this.resourceCreator = resourceCreator;
    }

    public Resource createCdrFile() {
        log.info("CdrCreator: start create call information");
        Collection<Long> numbers = phoneNumberCreator.createSetOfPhoneNumbers();
        Collection<CallInformation> calls = callInfoCreator.createSetOfCallInformation(numbers);
        Resource resource = resourceCreator.createResourceFromCalls(calls);
        cdrFileWriter.createPhysicalFile(resource);
        return resource;
    }

    public Resource createCdrFileByPhoneNumbers(PhoneNumbersDto phoneNumbers) {
        log.info("CdrCreator: start create call information");
        Collection<CallInformation> calls = callInfoCreator.createSetOfCallInformation(phoneNumbers.numbers());
        Resource resource = resourceCreator.createResourceFromCalls(calls);
        cdrFileWriter.createPhysicalFile(resource);
        return resource;
    }
}
