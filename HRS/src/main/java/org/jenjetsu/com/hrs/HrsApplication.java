package org.jenjetsu.com.hrs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"org.jenjetsu.com.core","org.jenjetsu.com.hrs"})
@EnableDiscoveryClient
@EnableCaching
public class HrsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrsApplication.class, args);
    }

}
