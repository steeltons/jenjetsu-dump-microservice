package org.jenjetsu.com.core.service.implementation;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.jenjetsu.com.core.dto.*;
import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.Tariff;
import org.jenjetsu.com.core.repository.AbonentRepository;
import org.jenjetsu.com.core.service.AbonentService;
import org.jenjetsu.com.core.service.TariffService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@CacheConfig(cacheNames = "abonent-cache")
public class AbonentServiceImpl implements AbonentService {

    private final AbonentRepository abonentRep;
    private final TariffService tariffService;
    private final TransactionTemplate template;
    private final ConcurrentMapCacheManager cacheManager;

    public AbonentServiceImpl(AbonentRepository abonentRep,
                              TariffService tariffService,
                              TransactionTemplate template,
                              ConcurrentMapCacheManager manager) {
        this.abonentRep = abonentRep;
        this.tariffService = tariffService;
        this.template = template;
        this.cacheManager = manager;
    }


    @Override
    public boolean isExistByPhoneNumber(Long phoneNumber) {
        if(phoneNumber == null) {
            return false;
        }
        if(cacheManager.getCache("abonent-cache") != null &&
                cacheManager.getCache("abonent-cahce").get(phoneNumber) != null) {
            return true;
        }
        return abonentRep.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean isExistById(Long id) {
        return id != null && abonentRep.existsById(id);
    }

    @Override
    public void create(AbonentDto dto) {
        if(!dto.isValid()) {
            throw new IllegalArgumentException("Abonent is not valid");
        }
        if(isExistByPhoneNumber(dto.numberPhone())) {
            throw new EntityExistsException("Abonent is exists");
        }
        abonentRep.save(dto.convetToAbonent());
    }

    @Override
    public void createAll(Collection<AbonentDto> abonents) {
        template.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        template.executeWithoutResult((status) -> {
            try {
                abonents.forEach(abonent -> {
                    if(!abonent.isValid()) {
                        throw new IllegalArgumentException(String.format("Abonent %d is not valid", abonent.numberPhone()));
                    }
                    if(isExistByPhoneNumber(abonent.numberPhone())) {
                        throw new EntityExistsException(String.format("Abonent %d is exists", abonent.numberPhone()));
                    }
                    abonentRep.save(abonent.convetToAbonent());
                });
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    @Override
    @Cacheable(key = "#result.phoneNumber")
    public Abonent findById(Long id) {
        return abonentRep.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("Entity Abonent with id %d not found", id)));
    }

    @Override
    @Cacheable(key = "#phoneNumber")
    public Abonent findByPhoneNumber(Long phoneNumber) {
        Abonent a = abonentRep.findByPhoneNumber(phoneNumber).orElseThrow(() -> new EntityNotFoundException(
                String.format("Entity Abonent with phone number %d not found", phoneNumber)));
        return a;
    }

    @Override
    public List<Abonent> findAll() {
        return abonentRep.findAll();
    }

    @Override
    @CachePut(key = "#result.phoneNumber")
    public Abonent update(Abonent abonent) {
        return abonentRep.save(abonent);
    }

    @Override
    @CacheEvict(key = "#abonent.phoneNumber")
    public boolean delete(Abonent abonent) {
        boolean res = false;
        if(isExistByPhoneNumber(abonent.getPhoneNumber())) {
            abonentRep.delete(abonent);
        }
        return res;
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean deleteById(Long id) {
        boolean res = false;
        if(isExistByPhoneNumber(id)) {
            abonentRep.deleteById(id);
        }
        return res;
    }

    @Override
    public PaymentDTO addMoney(AbonentDto abonentDto) {
        Abonent a = template.execute((status) -> {
            try {
                if(!abonentDto.isValid()) {
                    throw new IllegalArgumentException("Abonent dto is not valid");
                }
                Abonent abonent = findByPhoneNumber(abonentDto.numberPhone());
                abonent.addMoney(abonentDto.money());
                addAbonentToCahce(abonent);
                return abonent;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
        return new PaymentDTO(a.getId(), a.getPhoneNumber(), a.getBalance());
    }


    @Override
    public ChangeTariffDto changeTariff(AbonentDto dto) {
        Abonent a =template.execute((status) -> {
            try {
                Abonent abonent = findByPhoneNumber(dto.numberPhone());
                Tariff tariff = tariffService.findById(dto.tariffId());
                abonent.setTariff(tariff);
                addAbonentToCahce(abonent);
                return abonent;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
        return new ChangeTariffDto(a.getId(), a.getPhoneNumber(), a.getTariff().getId());
    }

    @Override
    @CachePut(key = "#result.phoneNumber")
    public Abonent removeMoney(AbonentDto dto) {
        Abonent a = template.execute((status) -> {
            try {
                Abonent abonent = findByPhoneNumber(dto.numberPhone());
                abonent.subMoney(dto.money());
                return abonent;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
        return a;
    }

    private void addAbonentToCahce(Abonent abonent) {
        if(cacheManager.getCache("abonent-cahce") == null) {
            cacheManager.setCacheNames(Arrays.asList("abonent-cache"));
        }
        cacheManager.getCache("abonent-cache").put(abonent.getPhoneNumber(), abonent);
    }
}
