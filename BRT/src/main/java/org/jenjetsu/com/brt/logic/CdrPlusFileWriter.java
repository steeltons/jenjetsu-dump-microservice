package org.jenjetsu.com.brt.logic;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.dto.CdrDto;
import org.jenjetsu.com.core.exception.CdrPlusCreateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;
import java.util.UUID;

/**
 * <h2>File manipulator</h2>
 * Class which write cdrDto list to cdr+ file
 */
@Service
@Slf4j
public class CdrPlusFileWriter {

    private final File cdrPlusDirectory;
    private final boolean CDR_PLUS_FILE_WRITE;

    public CdrPlusFileWriter(@Value("${jenjetsu.debug.show-cdr-file}") boolean cdr_plus_file_write,
                             @Value("${jenjetsu.debug.cdr-file-path}") String filePath) {
        CDR_PLUS_FILE_WRITE = cdr_plus_file_write;
        if(filePath.isBlank()) {
            filePath = "src/main/resources/cdr plus/";
        }
        cdrPlusDirectory = new File(filePath);
    }

    /**
     * <h2>Write Cdr dtos to resource</h2>
     * Method which write list od Cdr Dto to resource.
     * @param cdrDtos - list of CdrDto
     * @return resourse - byte file
     */
    public Resource writeCdrDtosToResource(Collection<CdrDto> cdrDtos) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Iterator<CdrDto> iter = cdrDtos.iterator();
            while (iter.hasNext()) {
                CdrDto dto = iter.next();
                String line = String.format("%d %s\n",dto.phoneNumber(), dto.tariffId());
                out.write(line.getBytes(StandardCharsets.UTF_8));
                for(String call : dto.calls()) {
                    out.write((call + "\n").getBytes(StandardCharsets.UTF_8));
                }
                line = String.format("END %d CALLS"+(iter.hasNext() ? "\n" : ""), dto.phoneNumber());
                out.write(line.getBytes(StandardCharsets.UTF_8));
            }
            out.close();
            return new ByteArrayResource(out.toByteArray()) {
                @Override
                public String getFilename() {return UUID.randomUUID().toString()+".cdrPlus";}
            };
        } catch (Exception e) {
            log.error("CdrPlusFileWriter: ERROR WRITING CDR DTOS TO RESOURCE. Error message: {}", e.getMessage());
            throw new CdrPlusCreateException(String.format("Impossible to create cdr+ resource. Error message: %s", e.getMessage()));
        }
    }

    /**
     * <h2>Write Cdr resource to disk</h2>
     * Method that write cdr resource to disk
     * @param resource - byte file
     */
    public void writeCdrResourceToDisk(Resource resource) {
        if(!CDR_PLUS_FILE_WRITE) {
            return;
        }
        if(!cdrPlusDirectory.exists()) {
            cdrPlusDirectory.mkdirs();
        }
        String filePath = cdrPlusDirectory.getPath()+"/"+resource.getFilename();
        try(Scanner scanner = new Scanner(resource.getInputStream());
            FileWriter writer = new FileWriter(new File(filePath))) {
            while (scanner.hasNext()) {
                writer.write(scanner.nextLine()+(scanner.hasNext() ? "\n" : ""));
            }
            writer.flush();
            log.info("CdrPlusFileWriter: write resource to disk at path {}",filePath);
        } catch (Exception e) {
            log.error("FileManipulator: ERROR WRITING CDR PLUS FILE. Error message: {}", e.getMessage());
            throw new CdrPlusCreateException(String.format("Impossible to create cdr+ file. Error message: %s", e.getMessage()));
        }
    }
}
