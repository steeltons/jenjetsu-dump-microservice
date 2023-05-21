package org.jenjetsu.com.cdr2.broker.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <h2>Brt message sender</h2>
 * Send cdr file to BRT listener queue
 */
@Service
@Slf4j
public class BrtMessageSender {

    private final RabbitTemplate template;
    private final String brtCdrQueue;
    private final String brtAbonentsQueue;

    public BrtMessageSender(RabbitTemplate template,
                            @Value("${spring.rabbitmq.senders.brt-cdr-queue}") String brtCdrQueue,
                            @Value("${spring.rabbitmq.senders.brt-abonents-queue}") String brtAbonentsQueue) {
        this.template = template;
        this.brtCdrQueue = brtCdrQueue;
        this.brtAbonentsQueue = brtAbonentsQueue;
    }

    public void sendCdrFilenameToBrt(String s3Filename) {
        log.info("Send cdr s3 filename {} to BRT", s3Filename);
        template.convertAndSend(brtCdrQueue, s3Filename);
    }

    public void sendAbonentsToBrt(String s3Filename) {
        log.info("Send abonents s3 filename {} to BRT", s3Filename);
        template.convertAndSend(brtAbonentsQueue, s3Filename);
    }
}
