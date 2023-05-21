package org.jenjetsu.com.brt.broker.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <h2>HRS message sender</h2>
 * Send cdr+ file to HRS listener queue
 */
@Service
@Slf4j
public class HrsMessageSender {

    private final RabbitTemplate template;
    private final String hrsBillQueue;

    public HrsMessageSender(RabbitTemplate template,
                            @Value("${spring.rabbitmq.senders.hrs-bill-queue}") String hrsBillQueue) {
        this.template = template;
        this.hrsBillQueue = hrsBillQueue;
    }

    public void sendBillCommandToHrs(String s3Filename) {
        log.info("HrsMessageSender: send s3 filename {} to HRS", s3Filename);
        template.convertAndSend(hrsBillQueue, s3Filename);
    }
}
