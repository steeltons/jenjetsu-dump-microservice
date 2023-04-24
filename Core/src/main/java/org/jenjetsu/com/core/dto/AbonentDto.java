package org.jenjetsu.com.core.dto;

import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.entity.Tariff;

public record AbonentDto(Long numberPhone,
                         String tariffId,
                         double money) {

    public boolean isValid() {
        return numberPhone >= 70000000000l && numberPhone <= 89999999999l &&
                money >= 0 && money <= 10000;
    }

    public Abonent convetToAbonent() {
        Abonent abonent = new Abonent();
        abonent.setPhoneNumber(numberPhone);
        Tariff tariff = new Tariff();
        tariff.setId(tariffId);
        abonent.setTariff(tariff);
        abonent.setBalance(money);
        return abonent;
    }
}
