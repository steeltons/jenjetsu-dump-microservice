package org.jenjetsu.com.hrs.logic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jenjetsu.com.core.entity.CallInformation;

import java.util.List;

/**
 * <h2>Cdr entity</h2>
 * Simple entity for cdr plus file
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CdrPlusEntity {

    private Long phoneNumber;
    private String tariffId;
    private List<CallInformation> calls;

}
