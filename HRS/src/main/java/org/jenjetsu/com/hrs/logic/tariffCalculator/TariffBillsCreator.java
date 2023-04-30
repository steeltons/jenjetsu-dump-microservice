package org.jenjetsu.com.hrs.logic.tariffCalculator;

import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.BillEntity;
import org.jenjetsu.com.core.entity.CallInformation;

import java.util.List;

public interface TariffBillsCreator {

    public BillEntity billPayloads(Long phoneNumber, String tariffId, List<CallInformation> calls);

}
