package org.jenjetsu.com.hrs.logic;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.BillEntity;
import org.jenjetsu.com.core.exception.BillFileCreateException;
import org.jenjetsu.com.hrs.logic.entity.CdrPlusEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * <h2>Bill file writer</h2>
 * Class which write bill file
 */
@Service
@Slf4j
public class BillFileWriter {

    private final File fileDirectory;
    private final boolean BILL_FILE_WRITE;

    public BillFileWriter(@Value("${jenjetsu.debug.show-cdr-file}") boolean bill_file_write,
                          @Value("${jenjetsu.debug.cdr-file-path}") String filePath) {
        BILL_FILE_WRITE = bill_file_write;
        if(filePath.isBlank()) {
            filePath = "src/main/resources/bill";
        }
        fileDirectory = new File(filePath);
    }

    /**
     * <h2>Write bills to resource</h2>
     * Method which write bills to resource
     * @param billEntities - list of bills
     * @return resource - bill byte file
     */
    public Resource writeBillToResource(Collection<BillEntity> billEntities) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Iterator<BillEntity> iter = billEntities.iterator();
            while (iter.hasNext()) {
                BillEntity bill = iter.next();
                String line = bill.phoneNumber() + " " + bill.tariffId() + "\n";
                out.write(line.getBytes(StandardCharsets.UTF_8));
                for(AbonentPayload payload : bill.dtoList()) {
                    out.write((payload.toString()+"\n").getBytes(StandardCharsets.UTF_8));
                }
                out.write(("Total sum:"+bill.totalSum()+"\n").getBytes(StandardCharsets.UTF_8));
                line = "Monetary unit:"+bill.monetaryUnit()+((iter.hasNext()) ? "\n" : "");
                out.write(line.getBytes(StandardCharsets.UTF_8));
            }
            out.close();
            ByteArrayResource resource = new ByteArrayResource(out.toByteArray()) {
                public String getFilename () {return UUID.randomUUID().toString() + ".bill";}
            };
            log.info("BillFileWriter: write bills to resource");
            return resource;
        } catch (Exception e) {
            log.error("BillFileWriter: ERROR IN WRITING RESOURCE BILL FILE. Error message: {}", e.getMessage());
            throw new BillFileCreateException(String.format("Impossible to create bill file. Error message: %s", e.getMessage()));
        }
    }

    /**
     * <h2>Write to file</h2>
     * Method which write collection of BillEntities to bill file
     * @param resource - bill byte file
     */
    public void writeToFile(Resource resource) {
        if(!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        String filePath = fileDirectory.getPath()+"/"+ resource.getFilename();
        try(Scanner scanner = new Scanner(resource.getInputStream());
            FileWriter writer = new FileWriter(new File(filePath))) {
            while (scanner.hasNext()) {
                writer.write(scanner.nextLine()+(scanner.hasNext() ? "\n" : ""));
            }
            writer.flush();
            log.info("BillFileWriter: write bill file to disk at path {}", filePath);
        } catch (Exception e) {
            log.error("BillFileWriter: ERROR IN WRITING BILL FILE. Error message: {}", e.getMessage());
            throw new BillFileCreateException(String.format("Impossible to create bill file. Error message: %s", e.getMessage()));
        }
    }
}
