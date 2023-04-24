package org.jenjetsu.com.brt;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.brt.logic.BillingProcess;
import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.service.AbonentService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"org.jenjetsu.com.core", "org.jenjetsu.com.brt"})
@EnableDiscoveryClient
public class BrtApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(BrtApplication.class, args);
        context.getBean(BillingProcess.class).billAbonents();
    }

}
