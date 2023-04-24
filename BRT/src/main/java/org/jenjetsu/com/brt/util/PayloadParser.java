package org.jenjetsu.com.brt.util;

import lombok.SneakyThrows;
import org.jenjetsu.com.core.entity.AbonentPayload;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * <h2>AbonentPayload parser</h2>
 * Class that parse AbonentPayload
 */
public class PayloadParser {

    /**
     * <h2>Parse from line</h2>
     * Method that pasre AbonentPayload from line of format
     * (callType),(startDate),(endDate),(Duration),(Cost)
     * Dates has format [yyyy-MM-dd HH-mm-ss]
     * Duration has format [hh-mm-ss]
     * @param line
     * @return AbonentPayload
     */
    @SneakyThrows
    public static AbonentPayload parseFromLine(String line) {
        String[] params = line.split(",");
        AbonentPayload payload = new AbonentPayload();
        payload.setCallType(Byte.parseByte(params[0]));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        SimpleDateFormat durationFormat = new SimpleDateFormat("HH:mm:ss");
        payload.setStartTime(new Timestamp(dateFormat.parse(params[1]).getTime()));
        payload.setEndTime(new Timestamp(dateFormat.parse(params[2]).getTime()));
        payload.setDuration(Time.valueOf(params[3]));
        payload.setCost(Double.parseDouble(params[4]));
        return payload;
    }
}
