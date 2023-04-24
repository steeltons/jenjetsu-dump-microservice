package org.jenjetsu.com.core.service.implementation;

import jakarta.persistence.EntityNotFoundException;
import org.jenjetsu.com.core.entity.Tariff;
import org.jenjetsu.com.core.repository.TariffRepository;
import org.jenjetsu.com.core.service.TariffService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class TariffServiceImpl implements TariffService {

    public final TariffRepository tariffRepository;

    public TariffServiceImpl(TariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    @Override
    public void create(Tariff tariff) {
        if(isExistById(tariff.getId())) {
            throw new IllegalArgumentException("Tariff is already exists");
        }
        tariffRepository.save(tariff);
    }

    @Override
    public void createAll(Collection<Tariff> tariffs) {
        tariffRepository.saveAll(tariffs);
    }

    @Override
    public Tariff findById(String id) {
        return tariffRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("Tariff with id %s not found", id)));
    }

    @Override
    public List<Tariff> findAll() {
        return tariffRepository.findAll();
    }

    @Override
    public boolean isExistById(String id) {
        return id != null && !id.isBlank() && tariffRepository.existsById(id);
    }
}
