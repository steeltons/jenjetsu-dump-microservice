package org.jenjetsu.com.core.service;

import org.jenjetsu.com.core.dto.ReportDto;
import org.jenjetsu.com.core.entity.AbonentPayload;

public interface AbonentPayloadService extends CreateDao<AbonentPayload, Long>,
                                               ReadDao<AbonentPayload, Long>,
                                               DeleteDao<AbonentPayload, Long> {
    public boolean isExistById(Long id);
    public void deleteAll();
}
