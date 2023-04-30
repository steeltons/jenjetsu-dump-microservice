package org.jenjetsu.com.core.entity;

import java.util.List;

/**
 * <h2>Bill Entity</h2>
 * Class which collect information of abonent payloads and sum, that he spoke
 * @param phoneNumber
 * @param tariffId
 * @param dtoList - list of AbonentPayloads
 * @param totalSum
 * @param monetaryUnit
 */
public record BillEntity(Long phoneNumber,
                         String tariffId,
                         List<AbonentPayload> dtoList,
                         double totalSum,
                         String monetaryUnit) {
}
