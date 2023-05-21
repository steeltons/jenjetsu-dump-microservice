package org.jenjetsu.com.brt;

import org.jenjetsu.com.brt.broker.sender.CdrMessageSender;
import org.jenjetsu.com.core.config.EnableMinIo;
import org.jenjetsu.com.core.config.EnableRabbitMq;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = {"org.jenjetsu.com.core", "org.jenjetsu.com.brt"})
@EnableDiscoveryClient
@EnableCaching @EnableMinIo @EnableRabbitMq
public class BrtApplication {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(BrtApplication.class, args);
//        context.getBean(CdrMessageSender.class).sendGenerateCdrFileCommand();
    }

}
