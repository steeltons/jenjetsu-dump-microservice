package org.jenjetsu.com.core.service.implementation;

import jakarta.persistence.EntityNotFoundException;
import org.jenjetsu.com.core.entity.Tariff;
import org.jenjetsu.com.core.repository.TariffRepository;
import org.jenjetsu.com.core.service.TariffService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@CacheConfig(cacheNames = "tariff-cache")
public class TariffServiceImpl implements TariffService {

    public final TariffRepository tariffRepository;
    private final ConcurrentMapCacheManager cacheManager;

    public TariffServiceImpl(TariffRepository tariffRepository,
                             ConcurrentMapCacheManager cacheManager) {
        this.tariffRepository = tariffRepository;
        this.cacheManager = cacheManager;
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
    @Cacheable(key = "#id")
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
        if(id == null || id.isBlank()){
            return false;
        }
        if(cacheManager.getCache("tariff-cache") != null &&
                cacheManager.getCache("tariff-id").get(id) != null) {
            return true;
        }
        return tariffRepository.existsById(id);
    }
}
