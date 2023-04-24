package org.jenjetsu.com.core.dto;

import java.sql.Time;
import java.sql.Timestamp;

public record AbonentPayloadDto(Byte callType,
                                Timestamp startCalling,
                                Timestamp endCalling,
                                Time duration,
                                Double cost) {
}
