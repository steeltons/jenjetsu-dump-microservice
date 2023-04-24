package org.jenjetsu.com.core.service;

import org.jenjetsu.com.core.dto.AbonentDto;
import org.jenjetsu.com.core.dto.ChangeTariffDto;
import org.jenjetsu.com.core.dto.PaymentDTO;
import org.jenjetsu.com.core.dto.ReportDto;
import org.jenjetsu.com.core.entity.Abonent;

public interface AbonentService extends ReadDao<Abonent, Long>,
                                        CreateDao<AbonentDto, Long>,
                                        UpdateDao<Abonent>,
                                        DeleteDao<Abonent, Long>{

    public boolean isExistByPhoneNumber(Long phoneNumber);
    public boolean isExistById(Long id);
    public void authorizeAbonents();
    public PaymentDTO addMoney(AbonentDto dto);
    public ChangeTariffDto changeTariff(AbonentDto dto);
    public Abonent removeMoney(AbonentDto dto);
    public Abonent findByPhoneNumber(Long phoneNumber);

    public ReportDto getMyPayloads(Long phoneNumber);
}
