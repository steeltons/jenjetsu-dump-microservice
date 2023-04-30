package org.jenjetsu.com.hrs.logic.tariffCalculator;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.*;
import org.jenjetsu.com.core.service.TariffService;
import org.jenjetsu.com.core.util.AbonentPayloadParser;
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
    public BillEntity billPayloads(Long phoneNumber, String tariffId, List<CallInformation> calls) {
        Tariff tariff = tariffService.findById(tariffId);
        prepareTariffBiller(tariff);
        List<AbonentPayload> payloads = new ArrayList<>();
        for(CallInformation call : calls) {
            long callingDuration = call.getCallDurationInSeconds();
            AbonentPayload payload = AbonentPayloadParser.parseAbonentPayload(call);
            double sum = 0;
            if(payload.getCallType() == 1) {
                if(callBuffer != null && !callBuffer.minusSeconds(callingDuration).isNegative()) {
                    callBuffer = callBuffer.minusSeconds(callingDuration);
                    sum += TimeConverter.ceilSecondsToMinutes(callingDuration) * outcomingBufferCost;
                } else {
                    long lastBufferSeconds = 0;
                    if(callBuffer != null) {
                        lastBufferSeconds = callBuffer.getSeconds();
                        callBuffer = null;
                    }
                    sum += TimeConverter.ceilSecondsToMinutes(lastBufferSeconds) * outcomingBufferCost;
                    sum += TimeConverter.ceilSecondsToMinutes(Math.abs(callingDuration - lastBufferSeconds)) *  outcomingCost;
                }
            }
            payload.setCost(sum);
            payloads.add(payload);
        }
        double totalSum = payloads.stream().reduce(0.0, (accumulator, payload) -> accumulator + payload.getCost(),
                Double::sum);
        totalSum += tariff.getBasicPrice();
        totalSum = Math.ceil(totalSum * 100) / 100.0;
        return new BillEntity(phoneNumber, tariffId, payloads, totalSum, tariff.getMonetaryUnit());
    }

    private void prepareTariffBiller(Tariff tariff) {
        incomingCost = tariff.getInputCost();
        outcomingCost = tariff.getOutputCost();
        TariffOption newestOption = tariff.getLatestOption();
        if(tariff.getOptions() != null) {
            inconigBufferCost = newestOption.getIncomingBufferCost();
            outcomingBufferCost = newestOption.getOutcomingBufferCost();
            callBuffer = Duration.ofMinutes(newestOption.getTariffDurationMinutes());
        }
    }
}
