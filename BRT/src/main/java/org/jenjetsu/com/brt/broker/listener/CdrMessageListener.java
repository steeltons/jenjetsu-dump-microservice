package org.jenjetsu.com.brt.broker.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.brt.logic.CdrPlusFileManipulator;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * <h2>CDR message listener</h2>
 * Accept all messages from CDR microservice
 */
@Service
@AllArgsConstructor
@Slf4j
public class CdrMessageListener {

    private final CdrPlusFileManipulator cdrPlusFileManipulator;


    @RabbitListener(queues = "cdrQueue")
    public void handleCdrFile(String s3Filename){
        log.info("Got s3 cdr filename {} from CDR", s3Filename);
        Resource cdrPlusFile = cdrPlusFileManipulator.createCdrPlusFile(s3Filename);
        String cdrPlusFilename = cdrPlusFileManipulator.storeCdrPlusFileToS3Storage(cdrPlusFile);
        cdrPlusFileManipulator.sendCdrPlusFilenameToHrs(cdrPlusFilename);
    }

    @RabbitListener(queues = "abonentsQueue")
    public void handleAbonentsFile(String s3Filename) {
        log.info("Got s3 abonents filename {} from CDR", s3Filename);
    }
}
