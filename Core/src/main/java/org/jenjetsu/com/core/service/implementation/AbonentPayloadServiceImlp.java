package org.jenjetsu.com.core.service.implementation;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.jenjetsu.com.core.dto.AbonentPayloadDto;
import org.jenjetsu.com.core.dto.ReportDto;
import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.Tariff;
import org.jenjetsu.com.core.repository.AbonentPayloadRepository;
import org.jenjetsu.com.core.service.AbonentPayloadService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AbonentPayloadServiceImlp implements AbonentPayloadService {

    private final AbonentPayloadRepository payloadRep;

    public AbonentPayloadServiceImlp(AbonentPayloadRepository payloadRep) {
        this.payloadRep = payloadRep;
    }

    @Override
    public boolean isExistById(Long id) {
        return id != null && payloadRep.existsById(id);
    }

    @Override
    public void create(AbonentPayload abonentPayload) {
        if(isExistById(abonentPayload.getPayloadId())) {
            throw new IllegalArgumentException("Abonent payload is already exist");
        }
        payloadRep.save(abonentPayload);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void createAll(Collection<AbonentPayload> abonentPayloads) {
        payloadRep.saveAll(abonentPayloads);
    }

    @Override
    public boolean delete(AbonentPayload abonentPayload) {
        return deleteById(abonentPayload.getPayloadId());
    }

    @Override
    public boolean deleteById(Long id) {
        boolean res = false;
        if(isExistById(id)) {
            payloadRep.deleteById(id);
            res = true;
        }
        return res;
    }

    @Override
    public AbonentPayload findById(Long id) {
        return payloadRep.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Payload with id %d not found", id)));
    }

    @Override
    public List<AbonentPayload> findAll() {
        return payloadRep.findAll();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteAll() {
        payloadRep.deleteAll();
    }
}
