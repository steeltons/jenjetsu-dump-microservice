package org.jenjetsu.com.core.service.implementation;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.jenjetsu.com.core.dto.*;
import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.Tariff;
import org.jenjetsu.com.core.repository.AbonentRepository;
import org.jenjetsu.com.core.service.AbonentService;
import org.jenjetsu.com.core.service.TariffService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class AbonentServiceImpl implements AbonentService {

    private final AbonentRepository abonentRep;
    private final TariffService tariffService;

    public AbonentServiceImpl(AbonentRepository abonentRep, TariffService tariffService) {
        this.abonentRep = abonentRep;
        this.tariffService = tariffService;
    }


    @Override
    public boolean isExistByPhoneNumber(Long id) {
        return id != null && abonentRep.existsByPhoneNumber(id);
    }

    @Override
    public boolean isExistById(Long id) {
        return id != null && abonentRep.existsById(id);
    }

    @Override
    public void create(AbonentDto abonent) {
        if(!abonent.isValid()) {
            throw new IllegalArgumentException("Abonent is not valid") ;
        }
        if(isExistByPhoneNumber(abonent.numberPhone())) {
            throw new IllegalArgumentException("Abonent is exists");
        }
        abonentRep.save(abonent.convetToAbonent());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void createAll(Collection<AbonentDto> abonents) {
        abonents.forEach(abonent -> abonentRep.save(abonent.convetToAbonent()));
    }

    @Override
    public Abonent findById(Long id) {
        return abonentRep.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("Entity Abonent with id %d not found", id)));
    }

    @Override
    public Abonent findByPhoneNumber(Long phoneNumber) {
        return abonentRep.findByPhoneNumber(phoneNumber).orElseThrow(() -> new EntityNotFoundException(
                String.format("Entity Abonent with phone number %d not found", phoneNumber)));
    }

    @Override
    public List<Abonent> findAll() {
        return abonentRep.findAll();
    }

    @Override
    public Abonent update(Abonent abonent) {
        return abonentRep.save(abonent);
    }

    @Override
    public boolean delete(Abonent abonent) {
        boolean res = false;
        if(isExistByPhoneNumber(abonent.getPhoneNumber())) {
            abonentRep.delete(abonent);
        }
        return res;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean res = false;
        if(isExistById(id)) {
            abonentRep.deleteById(id);
        }
        return res;
    }


    @Override
    @Transactional
    public void authorizeAbonents() {
        abonentRep.deleteByBalanceLessThan(0);
    }

    @Override
    @Transactional
    public PaymentDTO addMoney(AbonentDto abonentDto) {
        if(!abonentDto.isValid()) {
            throw new IllegalArgumentException("Abonent dto is not valid");
        }
        Abonent abonent = findByPhoneNumber(abonentDto.numberPhone());
        abonent.addMoney(abonentDto.money());
        return new PaymentDTO(abonent.getId(), abonent.getPhoneNumber(), abonent.getBalance());
    }

    @Override
    @Transactional
    public ChangeTariffDto changeTariff(AbonentDto dto) {
        Abonent abonent = findByPhoneNumber(dto.numberPhone());
        Tariff tariff = tariffService.findById(dto.tariffId());
        abonent.setTariff(tariff);
        AbonentDto outDto = new AbonentDto(abonent.getPhoneNumber(), tariff.getId(), 0);
        return new ChangeTariffDto(abonent.getId(), abonent.getPhoneNumber(), abonent.getTariff().getId());
    }

    @Override
    @Transactional
    public Abonent removeMoney(AbonentDto dto) {
        Abonent abonent = findByPhoneNumber(dto.numberPhone());
        abonent.subMoney(dto.money());
        return abonent;
    }

    @Override
    public ReportDto getMyPayloads(Long phoneNumber) {
        Abonent abonent = findByPhoneNumber(phoneNumber);
        List<AbonentPayload> payloads = abonent.getPayloadList();
        Tariff tariff = abonent.getTariff();
        if(!payloads.isEmpty()) {
            abonent = payloads.get(0).getAbonent();
        }
        List<AbonentPayloadDto> dtos = payloads.stream().map(p -> new AbonentPayloadDto(p.getCallType(), p.getStartTime(),
                p.getEndTime(), p.getDuration(), p.getCost())).collect(Collectors.toList());
        double sum = dtos.stream().reduce(0.0, (temp, dto2) -> temp + dto2.cost(), Double::sum);
        return new ReportDto(abonent.getId(), abonent.getPhoneNumber(), dtos, sum, tariff.getMonetaryUnit());
    }
}
