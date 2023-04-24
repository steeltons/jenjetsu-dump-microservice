package org.jenjetsu.com.cdr2;

import lombok.SneakyThrows;
import org.jenjetsu.com.cdr2.logic.CallInfoCreator;
import org.jenjetsu.com.cdr2.logic.CdrFileCreator;
import org.jenjetsu.com.cdr2.logic.PhoneNumberCreator;
import org.jenjetsu.com.core.entity.CallInformation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.FileWriter;
import java.util.Scanner;
import java.util.Set;

@SpringBootApplication(scanBasePackages = {"org.jenjetsu.com.core","org.jenjetsu.com.cdr2"})

public class Cdr2Application {

    @SneakyThrows
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Cdr2Application.class, args);
        Resource res = context.getBean(CdrFileCreator.class).createCdrFile();
        FileWriter writer = new FileWriter("Test.txt");
        Scanner scanner = new Scanner(res.getInputStream());
        while (scanner.hasNext()) {
            writer.write(scanner.nextLine()+"\n");
        }
        writer.flush();
    }

}
