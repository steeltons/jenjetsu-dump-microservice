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
public class Tariff06BillCreator implements TariffBillsCreator{

    private final TariffService tariffService;
    private double incomingCost = 1;
    private double outcomingCost = 1;
    private double incomingBufferCost = 0;
    private double outcomingBufferCost = 0;
    private Duration callBuffer = Duration.ofMinutes(300);

    public Tariff06BillCreator(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @Override
    public BillEntity billPayloads(Long phoneNumber, String tariffId, List<CallInformation> calls) {
        Tariff tariff = tariffService.findById(tariffId);
        prepareTariffBiller(tariff);
        List<AbonentPayload> payloads = new ArrayList<>();
        for(CallInformation call : calls) {
            double sum = 0;
            AbonentPayload payload = AbonentPayloadParser.parseAbonentPayload(call);
            long callingDuration = call.getCallDurationInSeconds();
            if(callBuffer != null && !callBuffer.minusSeconds(callingDuration).isNegative()) {
                sum += TimeConverter.ceilSecondsToMinutes(callingDuration) *
                        (payload.getCallType() == 1 ? outcomingBufferCost : incomingBufferCost);
                callBuffer = callBuffer.minusSeconds(callingDuration);
            } else {
                long lastBufferSeconds = 0;
                if(callBuffer != null) {
                    lastBufferSeconds = callBuffer.getSeconds();
                    callBuffer = null;
                }
                callingDuration -= lastBufferSeconds;
                sum += TimeConverter.ceilSecondsToMinutes(lastBufferSeconds) *
                        (payload.getCallType() == 1 ? outcomingBufferCost : incomingBufferCost);
                sum += TimeConverter.ceilSecondsToMinutes(callingDuration) *
                        (payload.getCallType() == 1 ? outcomingCost : incomingCost);
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
            incomingBufferCost = newestOption.getIncomingBufferCost();
            outcomingBufferCost = newestOption.getOutcomingBufferCost();
            callBuffer = Duration.ofMinutes(newestOption.getTariffDurationMinutes());
        }
    }
}
