package org.jenjetsu.com.core.dto;

import java.util.Collection;

/**
 * <h2>Cdr dto</h2>
 * Class which collect simple information of abonent and his calls
 * @param phoneNumber
 * @param tariffId
 * @param calls - list of CallInformation
 */
public record CdrDto(Long phoneNumber, String tariffId, Collection<String> calls) {


}
