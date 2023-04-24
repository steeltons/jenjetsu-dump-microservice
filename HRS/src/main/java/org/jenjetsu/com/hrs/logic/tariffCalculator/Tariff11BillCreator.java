package org.jenjetsu.com.hrs.logic.tariffCalculator;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.core.entity.Tariff;
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
public class Tariff11BillCreator implements TariffBillsCreator{

    private final TariffService tariffService;
    private double incomingCost = 1.5;
    private double outcomingCost = 0;
    private double inconigBufferCost = 0.5;
    private double outcomingBufferCost = 0;
    private Duration callBuffer = Duration.ofMinutes(100);

    public Tariff11BillCreator(TariffService tariffService) {
        this.tariffService = tariffService;
    }


    @Override
    public List<AbonentPayload> billPayloads(String tariffId, List<CallInformation> calls) {
        prepareTariffBiller(tariffId);
        List<AbonentPayload> payloads = new ArrayList<>();
        for(CallInformation call : calls) {
            long callingDuration = call.getCallDurationInSeconds();
            AbonentPayload payload = AbonentPayloadParser.parseAbonentPayload(call);
            double sum = 0;
            if(payload.getCallType() == 1) {
                if(callBuffer != null && !callBuffer.minusSeconds(callingDuration).isNegative()) {
                    callBuffer = callBuffer.minusSeconds(callingDuration);
                    sum += TimeConverter.ceilSecondsToMinutes(callingDuration) * inconigBufferCost;
                } else {
                    long lastBufferSeconds = 0;
                    if(callBuffer != null) {
                        lastBufferSeconds = callingDuration - callBuffer.getSeconds();
                        callBuffer = null;
                    }
                    sum += TimeConverter.ceilSecondsToMinutes(lastBufferSeconds) * inconigBufferCost;
                    sum += TimeConverter.ceilSecondsToMinutes(callingDuration) *  incomingCost;
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
