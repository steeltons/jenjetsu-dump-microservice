package org.jenjetsu.com.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallInformation {

    private byte callType;
    private Long phoneNumber;
    private Long callTo;
    private Date startCallingTime;
    private Date endCallingTime;

    public long getCallDurationInSeconds() {
        return (endCallingTime.getTime() - startCallingTime.getTime()) / 1000;
    }

    @Override
    @SneakyThrows
    public String toString() {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return String.format("%d, %d, %s, %s", callType, phoneNumber, format.format(startCallingTime), format.format(endCallingTime));
    }

    @Override
    public int hashCode() {
        int res = 1;
        res = res * 31 + callType;
        res = res * 31 + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        res = res * 31 + (startCallingTime != null ? startCallingTime.hashCode() : 0);
        res = res * 31 + (endCallingTime != null ? endCallingTime.hashCode() : 0);
        return res;
    }
}
