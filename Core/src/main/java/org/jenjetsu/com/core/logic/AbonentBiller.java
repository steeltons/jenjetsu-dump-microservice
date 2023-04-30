package org.jenjetsu.com.core.logic;

import org.jenjetsu.com.core.dto.AbonentBillingDto;
import org.jenjetsu.com.core.dto.BillingDto;
import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.BillEntity;
import org.jenjetsu.com.core.service.AbonentPayloadService;
import org.jenjetsu.com.core.service.AbonentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AbonentBiller {

    private final AbonentService abonentService;
    private final AbonentPayloadService payloadService;
    private final TransactionTemplate template;

    public AbonentBiller(AbonentService abonentService,
                         AbonentPayloadService payloadService,
                         TransactionTemplate template) {
        this.abonentService = abonentService;
        this.payloadService = payloadService;
        this.template = template;
    }

    public BillingDto billAbonents(Collection<BillEntity> bills) {
        template.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        template.execute((status) -> {
            try {
                payloadService.deleteAll();
                List<Long> deletable = new ArrayList<>();
                List<Abonent> validableAbonents = new ArrayList<>();
                List<AbonentPayload> validablePayloads = new ArrayList<>();
                bills.forEach((bill) -> {
                    Abonent abonent = abonentService.findByPhoneNumber(bill.phoneNumber());
                    if(abonent.getBalance() < bill.totalSum()) {
                        deletable.add(abonent.getId());
                    } else {
                        abonent.subMoney(bill.totalSum());
                        validableAbonents.add(abonent);
                        bill.dtoList().forEach(payload -> payload.setAbonent(abonent));
                        validablePayloads.addAll(bill.dtoList());
                    }
                });
                deletable.forEach(abonentService::deleteById);
                validableAbonents.forEach(abonentService::update);
                validablePayloads.forEach(payloadService::create);
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
            return null;
        });
        List<Abonent> abonents = abonentService.findAll();
        return new BillingDto(abonents
                                        .stream()
                                        .map(a -> new AbonentBillingDto(a.getPhoneNumber(), a.getBalance()))
                                        .collect(Collectors.toList()));
    }
}
