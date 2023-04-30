package org.jenjetsu.com.core.util;

import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.CallInformation;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h2>Payload parser</h2>
 * Class that parse CallInfomation or List of CallInformation to AbonentPayload
 */
public class AbonentPayloadParser {

    /**
     * <h2>Parser abonent payload</h2>
     * Method that convert CallInformation to AbonentPayload
     * @param info - information of call
     * @return AbonentPayload
     */
    public static AbonentPayload parseAbonentPayload(CallInformation info) {
        AbonentPayload payload = new AbonentPayload();
        payload.setCallType(info.getCallType());
        payload.setStartTime(new Timestamp(info.getStartCallingTime().getTime()));
        payload.setEndTime(new Timestamp(info.getEndCallingTime().getTime()));
        payload.setDuration(new Time(14 * 60 * 60 * 1000 + info.getCallDurationInSeconds() * 1000));
        return payload;
    }
}
