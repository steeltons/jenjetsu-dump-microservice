package org.jenjetsu.com.cdr2.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.cdr2.broker.sender.BrtMessageSender;
import org.jenjetsu.com.core.dto.PhoneNumbersDto;
import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.core.service.S3Service;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
@AllArgsConstructor
public class CdrFileManipulator {

    private final CdrFileWriter cdrFileWriter;
    private final PhoneNumberCreator phoneNumberCreator;
    private final CallInfoCreator callInfoCreator;
    private final CdrFileResourceCreator resourceCreator;
    private final S3Service minioService;
    private final BrtMessageSender messageSender;

    public Resource createCdrFile() {
        log.info("CdrCreator: start create call information");
        Collection<Long> numbers = phoneNumberCreator.createSetOfPhoneNumbers();
        Collection<CallInformation> calls = callInfoCreator.createSetOfCallInformation(numbers);
        Resource cdrFile = resourceCreator.createResourceFromCalls(calls);
        return cdrFile;
    }

    public String saveCdrFileToStorage(Resource cdrFile) {
        log.info("CdrFileManipulator: save cdr file in storage");
        try {
            String cdrFilename = minioService.putObject(cdrFile.getFilename(), cdrFile.getInputStream());
            log.info("CdrFileManipulator: save cdr file {} in s3 storage", cdrFilename);
            return cdrFilename;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCdrFileToBrt(String cdrFilename) {
        messageSender.sendCdrFilenameToBrt(cdrFilename);
    }

    public Resource createCdrFileByPhoneNumbers(PhoneNumbersDto phoneNumbers) {
        log.info("CdrCreator: start create call information");
        Collection<CallInformation> calls = callInfoCreator.createSetOfCallInformation(phoneNumbers.numbers());
        Resource resource = resourceCreator.createResourceFromCalls(calls);
        cdrFileWriter.createPhysicalFile(resource);
        return resource;
    }
}
