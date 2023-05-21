package org.jenjetsu.com.brt.broker.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.brt.logic.BillFileParser;
import org.jenjetsu.com.brt.logic.BillingProcess;
import org.jenjetsu.com.core.dto.BillingDto;
import org.jenjetsu.com.core.entity.BillEntity;
import org.jenjetsu.com.core.logic.AbonentBiller;
import org.jenjetsu.com.core.service.S3Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <h2>HRS message listener</h2>
 * Accept all messages from HRS microservice
 */
@Service
@AllArgsConstructor
@Slf4j
public class HrsMessageListener {

    private final BillFileParser billFileParser;
    private final AbonentBiller abonentBiller;
    private final S3Service minioService;
    private final BillingProcess billingProcess;

    @RabbitListener(queues = "billQueue")
    public void handleBillFile(String s3Filename) {
        log.info("HrsMessageListener: Got bill filename {} from HRS", s3Filename);
        Resource billFile = null;
        try {
            billFile = minioService.getObject(s3Filename);
            minioService.removeObject(s3Filename);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Collection<BillEntity> billEntities = billFileParser.parseBillFileToBillEntities(billFile);
        log.info("Abonents validation");
        BillingDto billingDto = abonentBiller.billAbonents(billEntities);
        billingProcess.endBilling(billingDto);
    }
}
