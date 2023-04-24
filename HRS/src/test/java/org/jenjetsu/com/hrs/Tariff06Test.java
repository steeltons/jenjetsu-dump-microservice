package org.jenjetsu.com.hrs;

import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.hrs.logic.tariffCalculator.Tariff03BillCreator;
import org.jenjetsu.com.hrs.logic.tariffCalculator.Tariff06BillCreator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class Tariff06Test {

    @MockBean
    private Tariff06BillCreator tariff06BillCreator;

    @Test
    public void test1() {
    }
}
