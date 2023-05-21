package org.jenjetsu.com.brt.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.brt.broker.sender.HrsMessageSender;
import org.jenjetsu.com.core.dto.CdrDto;
import org.jenjetsu.com.core.service.S3Service;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class CdrPlusFileManipulator {

    private final PhoneCallGrouper phoneCallGrouper;
    private final CdrDtoConverter cdrDtoConverter;
    private final CdrPlusFileWriter cdrPlusFileWriter;
    private final S3Service minioService;
    private final HrsMessageSender hrsMessageSender;

    public Resource createCdrPlusFile(String cdrFilename) {
        try {
            Resource cdrFile = minioService.getObject(cdrFilename);
            minioService.removeObject(cdrFilename);
            Map<Long, List<String>> phoneCallsMap = phoneCallGrouper.groupPhoneCallsByPhone(cdrFile);
            Collection<CdrDto> cdrDtos = cdrDtoConverter.convertCallsMapToCdrs(phoneCallsMap);
            Resource cdrPlusFile = cdrPlusFileWriter.writeCdrDtosToResource(cdrDtos);
            return cdrPlusFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String storeCdrPlusFileToS3Storage(Resource cdrPlusFile) {
        log.info("CdrPlusFileManipulator: store cdr+ file to s3 storage");
        try {
            String filename = minioService.putObject(cdrPlusFile.getFilename(), cdrPlusFile.getInputStream());
            log.info("CdrPlusFileManipulator: success store cdr+ file {} to s3 storage", filename);
            return filename;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCdrPlusFilenameToHrs(String cdrPlusFilename) {
        log.info("CdrPlusFileManipulator: send s3 file {} to HSR", cdrPlusFilename);
        hrsMessageSender.sendBillCommandToHrs(cdrPlusFilename);
    }
}
