package org.jenjetsu.com.brt.logic;

import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.service.AbonentService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <h2>Abonent validator</h2>
 * Class that validate abonent by counting sum for calls
 */
@Service
public class AbonentValidator {


    private final AbonentService abonentService;
    public AbonentValidator(AbonentService abonentService) {
        this.abonentService = abonentService;
    }

    /**
     * <h2>Validate abonent</h2>
     * method that validate abonent by counting sum of calls
     * @param payloadList
     */
    public void validateAbonent(List<AbonentPayload> payloadList) {
        if(payloadList.isEmpty()){
            return;
        }
        Abonent abonent = payloadList.get(0).getAbonent();
        double sum = 0;
        for(AbonentPayload payload : payloadList) {
            sum += payload.getCost();
        }
        if(abonent.getBalance() < sum) {
            abonentService.deleteById(abonent.getId());
        } else {
            abonent.setPayloadList(payloadList);
            abonent.subMoney(sum);
            abonentService.update(abonent);
        }
    }
}
