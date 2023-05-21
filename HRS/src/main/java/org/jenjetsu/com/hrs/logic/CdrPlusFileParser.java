package org.jenjetsu.com.hrs.logic;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.core.exception.BillFileCreateException;
import org.jenjetsu.com.core.exception.CdrPlusCreateException;
import org.jenjetsu.com.hrs.logic.entity.CdrPlusEntity;
import org.jenjetsu.com.core.util.CallInformationParser;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * <h2>Cdr plus file parser</h2>
 * Class that parse cdr plus file to CdrEntity
 */
@Service
@Slf4j
public class CdrPlusFileParser {

    public List<CdrPlusEntity> parseCdrPlusFile(Resource cdrPlusFile) {
        List<CdrPlusEntity> cdrPlusEntities = new ArrayList<>();
        try(Scanner scanner = new Scanner(cdrPlusFile.getInputStream())) {
            if(!scanner.hasNext()) {
                throw new CdrPlusCreateException("File is empty");
            }
            while (scanner.hasNext()) {
                String[] params = scanner.nextLine().split(" ");
                Long phoneNumber = Long.parseLong(params[0]);
                String tariffId = params[1];
                String line = "";
                List<CallInformation> calls = new ArrayList<>();
                while (scanner.hasNext() && !(line = scanner.nextLine()).startsWith("END")) {
                    CallInformation call = CallInformationParser.parseCalInformation(line);
                    calls.add(call);
                }
                cdrPlusEntities.add(new CdrPlusEntity(phoneNumber, tariffId, calls));
            }
            log.info("CdrPlusFileParser: parse cdr+ file {}", cdrPlusFile.getFilename());
        } catch (Exception e) {
            log.error("CdrPlusFileParser: ERROR IN PRASING CDR PLUS FILE. Error message: {}", e.getMessage());
            throw new BillFileCreateException(String.format("Impossible to create bill file. Error message: %s", e.getMessage()));
        }
        return cdrPlusEntities;
    }
}
