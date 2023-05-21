package org.jenjetsu.com.core.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

public class RabbitConfiguration {

    private final String username;
    private final String password;
    private final String host;
    private final int port;
    private final List<String> queueNames;

    public RabbitConfiguration(@Value("${spring.rabbitmq.username}") String username,
                               @Value("${spring.rabbitmq.password}") String password,
                               @Value("${spring.rabbitmq.host}") String host,
                               @Value("${spring.rabbitmq.port}") int port,
                               @Value("${spring.rabbitmq.queue.names}") List<String> queueNames) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.queueNames = queueNames;
    }


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        AmqpAdmin admin = new RabbitAdmin(connectionFactory());
        queueNames.forEach((queueName) -> admin.declareQueue(new Queue(queueName)));
        return admin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

}
