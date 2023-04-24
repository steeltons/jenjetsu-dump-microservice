package org.jenjetsu.com.core.dto;

import org.jenjetsu.com.core.entity.AbonentPayload;

import java.util.List;

public record ReportDto(Long id,
                        Long phoneNumber,
                        List<AbonentPayloadDto> payloads,
                        double totalSum,
                        String monetaryUnit) {
}
