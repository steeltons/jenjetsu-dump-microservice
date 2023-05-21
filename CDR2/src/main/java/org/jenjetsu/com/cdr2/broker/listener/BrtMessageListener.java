package org.jenjetsu.com.cdr2.broker.listener;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.cdr2.broker.sender.BrtMessageSender;
import org.jenjetsu.com.cdr2.logic.CdrFileManipulator;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * <h2>Brt message listener</h2>
 * Listen commands from BRT
 */
@Service
@Slf4j
public class BrtMessageListener {

    private final BrtMessageSender messageSender;
    private final CdrFileManipulator cdrFileManipulator;

    public BrtMessageListener(BrtMessageSender messageSender,
                              CdrFileManipulator cdrFileManipulator) {
        this.cdrFileManipulator = cdrFileManipulator;
        this.messageSender = messageSender;
    }

    @RabbitListener(queues = "generateCdr")
    public void handleGenerateCdrCommandFromBrt(String command) {
        log.info("Got command {} from BRT to generate cdr file.", command);
        Resource cdrFile = cdrFileManipulator.createCdrFile();
        String cdrFilename = cdrFileManipulator.saveCdrFileToStorage(cdrFile);
        cdrFileManipulator.sendCdrFileToBrt(cdrFilename);
    }

    @RabbitListener(queues = "generateAbonents")
    public void handleGenerateAbonentsCommandFromBrt(String command) {
        log.info("Got command {} from BRT to generate abonents.", command);
    }
}
