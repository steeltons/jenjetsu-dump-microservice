package org.jenjetsu.com.cdr2.logic;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.CallInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

/**
 * <h2>Cdr file writer</h2>
 * Class that write cdr file repository resource folder
 * @deprecated - now all files saves at S3 storage.
 */
@Service
@Slf4j
@Deprecated(forRemoval = true)
public class CdrFileWriter {

    private final File fileDirectory;
    private final boolean SHOW_CDR_FILE;
    public CdrFileWriter(@Value("${jenjetsu.debug.show-cdr-file}") boolean show_cdr_file,
                         @Value("${jenjetsu.debug.cdr-file-path}") String cdrFilePath) {
        SHOW_CDR_FILE = show_cdr_file;
        if(cdrFilePath.isBlank()) {
            cdrFilePath = "src/main/resources/cdr";
        }
        this.fileDirectory = new File(cdrFilePath);
    }

    /**
     * <h2>Write calls and get path</h2>
     * Write resource into physic file
     * @param resource - byte version of cdr file
     * @deprecated - cdr file exists in s3 storage so there is no need to save file in system.
     */
    @Deprecated(forRemoval = true)
    public void createPhysicalFile(Resource resource) {
        if(!SHOW_CDR_FILE) {
            return;
        }
        if(!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        String filePath  = fileDirectory.getPath()+"/"+ resource.getFilename();
        try(Scanner scanner = new Scanner(resource.getInputStream());
            FileWriter writer = new FileWriter(filePath)) {
            while (scanner.hasNext()) {
                writer.write(scanner.nextLine() + (scanner.hasNext() ? "\n" : ""));
            }
            log.info("CdrFileWriter: write cdr file to disk at path {}", filePath);
        } catch (Exception e) {
            log.error("CdrFileCreator: ERROR WRITING CDR FILE. Message: {}", e.getMessage());
        }
    }
}
