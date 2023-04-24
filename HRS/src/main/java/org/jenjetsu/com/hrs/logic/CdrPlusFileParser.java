package org.jenjetsu.com.hrs.logic;

import lombok.SneakyThrows;
import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.hrs.logic.entity.CdrPlusEntity;
import org.jenjetsu.com.hrs.util.CallInformationParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * <h2>Cdr plus file parser</h2>
 * Class that parse cdr plus file to CdrEntity
 */
@Service
public class CdrPlusFileParser {

    /**
     * <h2>Parse file</h2>
     * This method parse cdr plus file to CdrEntity
     * @param cdrPlusFile
     * @return CdrEntity
     */
    @SneakyThrows
    public CdrPlusEntity parseFile(MultipartFile cdrPlusFile) {
        CdrPlusEntity cdrPlusEntity = new CdrPlusEntity();
        if(cdrPlusFile.isEmpty()){
            throw new IllegalArgumentException("Cdr plus file is empty");
        }
        long phoneNumber = 0;
        String tariffId = "";

        Scanner scanner = new Scanner(cdrPlusFile.getInputStream());
        List<CallInformation> calls = new ArrayList<>();
        String[] params = scanner.nextLine().split(" ");
        phoneNumber = Long.parseLong(params[0]);
        tariffId = params[1];
        if(!scanner.hasNext()){
            throw new IllegalArgumentException("File contains only phone number and tariff id");
        }
        while (scanner.hasNext()) {
            CallInformation info = CallInformationParser.parseCalInformation(scanner.nextLine());
            calls.add(info);
        }
        cdrPlusEntity.setPhoneNumber(phoneNumber);
        cdrPlusEntity.setTariffId(tariffId);
        cdrPlusEntity.setCalls(calls);
        return cdrPlusEntity;
    }

}
