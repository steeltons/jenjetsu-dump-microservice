package org.jenjetsu.com.core.logic;

import org.jenjetsu.com.core.dto.AbonentPayloadDto;
import org.jenjetsu.com.core.dto.ReportDto;
import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.service.AbonentPayloadService;
import org.jenjetsu.com.core.service.AbonentService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AbonentReportCreator {

    private final AbonentService abonentService;
    private final AbonentPayloadService payloadService;

    public AbonentReportCreator(AbonentService abonentService,
                                AbonentPayloadService payloadService) {
        this.abonentService = abonentService;
        this.payloadService = payloadService;
    }

    public ReportDto getMyPayloads(Long phoneNumber) {
        Abonent abonent = abonentService.findByPhoneNumber(phoneNumber);
        Collection<AbonentPayload> payloads = payloadService.findMyPayloads(phoneNumber);
        List<AbonentPayloadDto> payloadDtos = payloads.stream().map(p -> new AbonentPayloadDto(p.getCallType(), p.getStartTime(),
                p.getEndTime(), p.getDuration(), p.getCost())).collect(Collectors.toList());
        double totalSum = payloadDtos.stream().reduce(0.0, (acc, p) -> acc + p.cost(), Double::sum);
        totalSum += abonent.getTariff().getBasicPrice();
        return new ReportDto(abonent.getId(), abonent.getPhoneNumber(), payloadDtos, totalSum, abonent.getTariff().getMonetaryUnit());
    }
}
