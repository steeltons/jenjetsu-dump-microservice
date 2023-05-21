package org.jenjetsu.com.hrs.broker.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.hrs.logic.BillFileManipulator;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * <h2>Brt message listener</h2>
 * Accept BRT messages to parse cdr+ file
 */
@Service
@AllArgsConstructor
@Slf4j
public class BrtMessageListener {

    private final BillFileManipulator billFileManipulator;

    @RabbitListener(queues = "generateBill")
    public void handleCdrPlusFile(String s3Filename) {
        Resource billFile = billFileManipulator.createBillFile(s3Filename);
        String billFilename = billFileManipulator.storeBillFile(billFile);
        billFileManipulator.sendBillFileToBrt(billFilename);
    }
}
