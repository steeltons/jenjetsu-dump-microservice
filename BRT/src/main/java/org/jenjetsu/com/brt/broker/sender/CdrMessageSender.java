package org.jenjetsu.com.brt.broker.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <h2>CDR message sender</h2>
 * Send commands to CDR listener queue
 */
@Service
@Slf4j
public class CdrMessageSender {

    private final RabbitTemplate template;
    private final String cdrGenerateQueue;
    private final String abonentGenerateQueue;

    public CdrMessageSender(RabbitTemplate template,
                            @Value("${spring.rabbitmq.senders.cdr-cdr-queue}") String cdrGenerateQueue,
                            @Value("${spring.rabbitmq.senders.cdr-abonent-queue}")String abonentGenerateQueue) {
        this.template = template;
        this.cdrGenerateQueue = cdrGenerateQueue;
        this.abonentGenerateQueue = abonentGenerateQueue;
    }

    public void sendGenerateCdrFileCommand() {
        String command = "generate";
        log.info("Send command \"{}\" to CRM microservice", command);
        template.convertAndSend(cdrGenerateQueue, command);
    }

    public void sendGenerateAbonentsCommand() {
        String command = "abonents";
        log.info("Send command \"{}\" to CRM microservice");
        template.convertAndSend(abonentGenerateQueue, command);
    }
}
