package org.jenjetsu.com.cdr2;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.CallInformation;

@Slf4j
public class CallInfoValidator {

    public static boolean isCallValid(CallInformation call) {
        boolean res = true;
        if(call.getCallType() != 1 && call.getCallType() != 2) {
            log.warn("Call {} has callType {}", call.toString(), call.getCallType());
            res = false;
        }
        if(call.getStartCallingTime().compareTo(call.getEndCallingTime()) >= 0) {
            log.warn("Call {} startCalling time is after endCalling time", call.toString());
            res = false;
        }
        if(call.getCallDurationInSeconds() <= 0) {
            log.warn("Call {} callDuration is less or equal zero", call.toString());
            res = false;
        }
        if(call.getCallTo() != null && call.getCallTo().equals(call.getPhoneNumber())) {
            log.warn("I'm calling to myself. PhoneNumber: {}. CallTo: {}", call.getPhoneNumber(), call.getCallTo());
        }
        return res;
    }
}
