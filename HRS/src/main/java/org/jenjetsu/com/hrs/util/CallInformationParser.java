package org.jenjetsu.com.hrs.util;

import lombok.SneakyThrows;
import org.jenjetsu.com.core.entity.CallInformation;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <h2>Call information parser</h2>
 * Class that parse String to CallInformation
 */
public class CallInformationParser {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    /**
     * <h2>Parse call information</h2>
     * Method that parse String line to CallInformation
     * @param line
     * @return CallInformation
     */
    public static CallInformation parseCalInformation(String line) {
        String[] params = line.replace(" ","").split(",");
        return parseCallInformation(params);
    }

    /**
     * <h2>Parse call information</h2>
     * Method that parse array of String to CallInformation
     * @param params - String array
     * @return CallInformation
     */
    @SneakyThrows
    public static CallInformation parseCallInformation(String[] params) {
        CallInformation information = new CallInformation();
        information.setCallType(Byte.parseByte(params[0]));
        information.setPhoneNumber(Long.parseLong(params[1]));
        information.setStartCallingTime(dateFormat.parse(params[2]));
        information.setEndCallingTime(dateFormat.parse(params[3]));
        return information;
    }

}
