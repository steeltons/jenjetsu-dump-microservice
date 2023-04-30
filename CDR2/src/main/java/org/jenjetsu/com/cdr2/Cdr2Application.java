package org.jenjetsu.com.cdr2;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.FileWriter;
import java.util.Scanner;

@SpringBootApplication(scanBasePackages = {"org.jenjetsu.com.core.entity", "org.jenjetsu.com.core.util",
                                            "org.jenjetsu.com.core.exception", "org.jenjetsu.com.cdr2"})

public class Cdr2Application {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(Cdr2Application.class, args);
    }

}
