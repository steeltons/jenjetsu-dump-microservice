package org.jenjetsu.com.hrs.logic.tariffCalculator;


import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.BillEntity;
import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.core.entity.Tariff;
import org.jenjetsu.com.core.service.TariffService;
import org.jenjetsu.com.core.util.AbonentPayloadParser;
import org.jenjetsu.com.hrs.util.TimeConverter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
@Slf4j
public class Tariff03BillCreator implements TariffBillsCreator{

    private final TariffService tariffService;
    private double incomingCost = 0;
    private double outcomingCost = 0;

    public Tariff03BillCreator(TariffService tariffService) {
        this.tariffService = tariffService;
    }


    @Override
    public BillEntity billPayloads(Long phoneNumber, String tariffId, List<CallInformation> calls) {
        Tariff tariff = tariffService.findById(tariffId);
        incomingCost = tariff.getInputCost();
        outcomingCost = tariff.getOutputCost();
        List<AbonentPayload> payloads = new ArrayList<>();
        for(CallInformation call : calls) {
            AbonentPayload payload = AbonentPayloadParser.parseAbonentPayload(call);
            long callingSeconds = call.getCallDurationInSeconds();
            double sum = TimeConverter.ceilSecondsToMinutes(callingSeconds) * (call.getCallType() == 1 ? outcomingCost :
                    incomingCost);
            payload.setCost(sum);
            payloads.add(payload);
        }
        double totalSum = payloads.stream().reduce(0.0, (accumulator, payload) -> accumulator + payload.getCost(),
                Double::sum);
        totalSum+= tariff.getBasicPrice();
        totalSum = Math.ceil(totalSum * 100) / 100.0;
        return new BillEntity(phoneNumber, tariffId, payloads, totalSum, tariff.getMonetaryUnit());
    }

}
