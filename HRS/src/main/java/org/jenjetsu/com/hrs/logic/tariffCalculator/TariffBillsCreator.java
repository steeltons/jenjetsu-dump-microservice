package org.jenjetsu.com.hrs.logic.tariffCalculator;

import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.CallInformation;

import java.util.List;

public interface TariffBillsCreator {

    public List<AbonentPayload> billPayloads(String tariffId, List<CallInformation> calls);

}
