package org.jenjetsu.com.hrs;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.core.entity.Tariff;
import org.jenjetsu.com.core.entity.TariffOption;
import org.jenjetsu.com.core.service.TariffService;
import org.jenjetsu.com.hrs.logic.tariffCalculator.Tariff06BillCreator;
import org.jenjetsu.com.hrs.util.CallInformationParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class Tariff06Test {

    @Mock
    private TariffService tariffService;
    @InjectMocks
    private Tariff06BillCreator tariff06BillCreator;
    private final Tariff changedTariff = new Tariff("06", 0, 1.5, 5,
                                                        new TariffOption(null, null, 1,
                                                                2, 100,
                                                                false
                                                        ),
                                            "rubbles");

    @BeforeEach
    public void init() {
        when(tariffService.findById(any())).thenReturn(new Tariff(
                                                                "06", 100, 1, 1,
                                                                    new TariffOption(null, null, 0,
                                                                            0, 300, false
                                                                    ),
                                                        "rubbles"));
        tariff06BillCreator = new Tariff06BillCreator(tariffService);
    }

    private List<CallInformation> getCallsFromFile(File file) {
        List<CallInformation> calls = new ArrayList<>();
        try(Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                CallInformation call = CallInformationParser.parseCallInformation(line.replace(" ","").split(","));
                calls.add(call);
            }
        } catch (IOException e) {
            log.error("File not found at path {}", file.getAbsolutePath());
        }
        return calls;
    }

    private double calculateSum(List<CallInformation> calls) {
        List<AbonentPayload> payloads = tariff06BillCreator.billPayloads("06",calls);
        double sum = 0;
        for(AbonentPayload payload : payloads) {
            sum += payload.getCost();
        }
        return sum;
    }

    /**
     * <h2>Проверка жизнеспособности алгоритма</h2>
     * Проверяет то, работает ли алгоритм вообще
     * <h3>Всего длительность звонков - 302 минуты (300 + 1 + 2)</h3>
     */
    @Test
    public void workTest() {
        List<CallInformation> calls = getCallsFromFile(new File("src/test/java/org/jenjetsu/com/hrs/filetest/" +
                                                                        "goodTest.txt"));
        double sum = calculateSum(calls);
        assertEquals(4.0, sum);
    }

    /**
     * <h2>Проверка округления секунд в большую сторону</h2>
     * Проверяет то, работает ли округление секунд в большую сторону при звонке
     * Пример: 1 секунда = 1 минуте, и 59 секунд = 1 минуте
     */
    @Test
    public void ceilSecondsTest() {
        List<CallInformation> calls = getCallsFromFile(new File("src/test/java/org/jenjetsu/com/hrs/filetest/" +
                                                                        "ceilFileTest.txt"));
        double sum = calculateSum(calls);
        assertEquals(1.0, sum);
    }

    /**
     * <h2>Еврейский тест</h2>
     * Говорим 299 минут и 55 секунд, затем 6 секунд. Т.к. буфер после первого звонка
     * остался в 5 секунт, то мы во время второго звонка заплатим 1 рубль.
     * <h3>Всего длительность звонка - 300 минут и 1 секунда (04:59:55 + 00:00:06</h3>
     */
    public void checkJewTrick() {
        List<CallInformation> calls = getCallsFromFile(new File("src/test/java/org/jenjetsu/com/hrs/filetest/" +
                                                                        "jewTrick.txt"));
        double sum = calculateSum(calls);
        assertEquals(1.0, sum);
    }

    /**
     * <h2>Проверка для подсчёта стоимости исходящих звонков</h2>
     * Проверяет работоспособность алгоритма при только исзодящих звонках
     * <h3>Всего длительность звонков - 319 минут (300 + 1 + 1 + 2 + 15)</h3>
     */
    @Test
    public void onlyInputCallsTest() {
        List<CallInformation> calls = getCallsFromFile(new File("src/test/java/org/jenjetsu/com/hrs/filetest/" +
                                                                        "onlyInputCallsTest.txt"));
        double sum = calculateSum(calls);
        assertEquals(19.0, sum);
    }

    /**
     * <h2>Проверка для подсчёта стоимости входящих звонков</h2>
     * Проверяет работоспособность алгоритма при только входящих звонках
     * <h3>Всего длительность звонков - 319 минут (300 + 1 + 1 + 2 + 15)</h3>
     */
    @Test
    public void onlyOutputCalls() {
        List<CallInformation> calls = getCallsFromFile(new File("src/test/java/org/jenjetsu/com/hrs/filetest/" +
                                                                        "onlyOutputCallsTest.txt"));
        double sum = calculateSum(calls);
        assertEquals(19.0, sum);
    }

    /**
     * <h2>Тест исходящих звонков для обновлённого тарифа</h2>
     * Проверяет работоспособность алгоритма, если параметры тарифа были изменёны в бд
     * <h3>Всего длительность звонков - 319 минут (300 + 1 + 1 + 2 + 15)</h3>
     * <h3>Тариф : 100 мин входящие: 1, исходящие: 2, остальные входящие: 1.5, исходящие: 5</h3>
     */
    @Test
    public void changeTariffTest() {
        when(tariffService.findById(any())).thenReturn(changedTariff);
        List<CallInformation> calls = getCallsFromFile(new File("src/test/java/org/jenjetsu/com/hrs/filetest/" +
                                                                        "onlyInputCallsTest.txt"));
        double sum = calculateSum(calls);
        assertEquals(428.5, sum);
    }

    /**
     * <h2>Тест входящих звонков для обновлённого тарифа</h2>
     * Проверяет работоспособность алгоритма, если параметры тарифа были изменёны в бд
     * <h3>Всего длительность звонков - 319 минут (300 + 1 + 1 + 2 + 15)</h3>
     * <h3>Тариф : 100 мин входящие: 1, исходящие: 2, остальные входящие: 1.5, исходящие: 5</h3>
     */
    @Test
    public void outputChangeTariffTest() {
        when(tariffService.findById(any())).thenReturn(changedTariff);
        List<CallInformation> calls = getCallsFromFile(new File("src/test/java/org/jenjetsu/com/hrs/filetest/" +
                                                                        "onlyOutputCalls.txt"));
        double sum = calculateSum(calls);
        assertEquals(1295.0, sum);
    }

}
