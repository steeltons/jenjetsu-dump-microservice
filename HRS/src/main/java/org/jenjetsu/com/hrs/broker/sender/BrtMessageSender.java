package org.jenjetsu.com.hrs.broker.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <h2>BRT message sender</h2>
 * Send bill file to BRT queue listener
 */
@Service
@Slf4j
public class BrtMessageSender {

    private final RabbitTemplate rabbitTemplate;
    private final String brtBillQueue;

    public BrtMessageSender(RabbitTemplate rabbitTemplate,
                            @Value("${spring.rabbitmq.senders.brt-bill-queue}") String brtBillQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.brtBillQueue = brtBillQueue;
    }

    public void sendBillFilenameToBrt(String s3Filename) {
        log.info("BrtMessageSender: send s3 file {} to BRT", s3Filename);
        rabbitTemplate.convertAndSend(brtBillQueue, s3Filename);
    }
}
