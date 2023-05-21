package org.jenjetsu.com.hrs;

import org.jenjetsu.com.core.config.EnableMinIo;
import org.jenjetsu.com.core.config.EnableRabbitMq;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"org.jenjetsu.com.core","org.jenjetsu.com.hrs"})
@EnableDiscoveryClient
@EnableCaching @EnableMinIo @EnableRabbitMq
public class HrsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrsApplication.class, args);
    }

}
