package org.jenjetsu.com.brt.logic;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.BillEntity;
import org.jenjetsu.com.core.exception.BillReadFileException;
import org.jenjetsu.com.core.util.AbonentPayloadParser;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * <h2>Bill file parser</h2>
 * Class which parse bill file to collection of BillEntity
 */
@Service
@Slf4j
public class BillFileParser {

    /**
     * <h2>Parse bill file to bill entities</h2>
     * @param billFile - bill byte file
     * @return collection of billEntity
     */
    public Collection<BillEntity> parseBillFileToBillEntities(Resource billFile) {
        List<BillEntity> reportDtoList = new ArrayList<>();
        try(Scanner scanner = new Scanner(billFile.getInputStream())) {
            while (scanner.hasNext()) {
                String[] params = scanner.nextLine().split(" ");
                Long phoneNumber = Long.parseLong(params[0]);
                String tariffId = params[1];

                List<AbonentPayload> abonentPayloads = new ArrayList<>();
                String line = "";
                while (scanner.hasNext() && !(line = scanner.nextLine()).toLowerCase().startsWith("total sum")) {
                    AbonentPayload dto = AbonentPayloadParser.parseFromLine(line);
                    abonentPayloads.add(dto);
                }

                double totalSum = Double.parseDouble(line.split(":")[1]);
                String monetaryUnit = scanner.nextLine().split(":")[1];
                reportDtoList.add(new BillEntity(phoneNumber, tariffId, abonentPayloads, totalSum, monetaryUnit));
            }
        } catch (Exception e) {
            log.error("BillFileParser: ERROR IN PARSING BILL FILE. Error message: {}",e.getMessage());
            throw new BillReadFileException(String.format("Impossible to read bill file. error message %s", e.getMessage()));
        }
        return reportDtoList;
    }

}
