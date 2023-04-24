package org.jenjetsu.com.hrs.logic.tariffCalculator;

import com.auth0.jwt.interfaces.Payload;
import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.core.entity.Tariff;
import org.jenjetsu.com.core.service.AbonentService;
import org.jenjetsu.com.core.service.TariffService;
import org.jenjetsu.com.hrs.util.AbonentPayloadParser;
import org.jenjetsu.com.hrs.util.TimeConverter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
@Slf4j
public class Tariff06BillCreator implements TariffBillsCreator{

    private final TariffService tariffService;
    private double incomingCost = 1;
    private double outcomingCost = 1;
    private double inconigBufferCost = 0;
    private double outcomingBufferCost = 0;
    private Duration callBuffer = Duration.ofMinutes(300);

    public Tariff06BillCreator(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @Override
    public List<AbonentPayload> billPayloads(String tariffId, List<CallInformation> calls) {
        prepareTariffBiller(tariffId);
        List<AbonentPayload> payloads = new ArrayList<>();
        for(CallInformation call : calls) {
            double sum = 0;
            AbonentPayload payload = AbonentPayloadParser.parseAbonentPayload(call);
            long callingDuration = call.getCallDurationInSeconds();
            if(callBuffer != null && !callBuffer.minusSeconds(callingDuration).isNegative()) {
                sum += TimeConverter.ceilSecondsToMinutes(callingDuration) *
                        (payload.getCallType() == 1 ? inconigBufferCost : outcomingBufferCost);
                callBuffer = callBuffer.minusSeconds(TimeConverter.ceilSecondsToMinutes(callingDuration));
            } else {
                long lastBufferSeconds = 0;
                if(callBuffer != null) {
                    lastBufferSeconds = callingDuration - callBuffer.getSeconds();
                    callBuffer = null;
                    sum += TimeConverter.ceilSecondsToMinutes(lastBufferSeconds) *
                            (payload.getCallType() == 1 ? inconigBufferCost : outcomingBufferCost);
                    sum += TimeConverter.ceilSecondsToMinutes(callingDuration) *
                            (payload.getCallType() == 1 ? incomingCost : outcomingCost);
                }
            }
            payload.setCost(sum);
            payloads.add(payload);
        }
        return payloads;
    }

    private void prepareTariffBiller(String tariffId) {
        Tariff tariff = tariffService.findById(tariffId);
        incomingCost = tariff.getInputCost();
        outcomingCost = tariff.getOutputCost();
        if(tariff.getOptions() != null) {
            inconigBufferCost = tariff.getOptions().getIncomingBufferCost();
            outcomingBufferCost = tariff.getOptions().getOutcomingBufferCost();
            callBuffer = Duration.ofMinutes(tariff.getOptions().getTariffDurationMinutes());
        }
    }
}
