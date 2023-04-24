package org.jenjetsu.com.core.service;

import org.jenjetsu.com.core.entity.Tariff;

public interface TariffService extends CreateDao<Tariff, String>,
                                       ReadDao<Tariff, String>{

    public boolean isExistById(String id);
}
