package org.jenjetsu.com.hrs;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.BillEntity;
import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.core.entity.Tariff;
import org.jenjetsu.com.core.entity.TariffOption;
import org.jenjetsu.com.core.service.TariffService;
import org.jenjetsu.com.core.util.CallInformationParser;
import org.jenjetsu.com.hrs.logic.tariffCalculator.Tariff11BillCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class Tariff11Test {

    @Mock
    private TariffService tariffService;
    private Tariff11BillCreator tariff11BillCreator;

    private TariffOption nativeTariffOption = new TariffOption(0l, null, 0, 0.5, 100, false);
    private Tariff nativeTariff = new Tariff("11", 0, 0, 1.5, Arrays.asList(nativeTariffOption), "rubbles");
    private TariffOption updatedTariffOption = new TariffOption(0l, null, 0, 1.5, 200, false);
    private Tariff updatedTariff = new Tariff("11", 25, 0, 2, Arrays.asList(updatedTariffOption), "krone");

    @BeforeEach
    void init() {
        when(tariffService.findById(any())).thenReturn(nativeTariff);
        tariff11BillCreator = new Tariff11BillCreator(tariffService);
    }

    @Test
    public void onlyInput100MinCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "100 min input call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff11BillCreator.billPayloads(phoneNumber, "11", calls);
        assertBill(bill, phoneNumber, "11", 0, "rubbles");
    }

    @Test
    public void onlyOutput100MinCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "100 min output call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff11BillCreator.billPayloads(phoneNumber, "11", calls);
        assertBill(bill, phoneNumber, "11", 50.0, "rubbles");
    }

    @Test
    public void onlyInput300MinCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "300 min input call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff11BillCreator.billPayloads(phoneNumber, "11", calls);
        assertBill(bill, phoneNumber, "11", 0.0, "rubbles");
    }

    @Test
    public void onlyOutput300MinCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "300 min output call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff11BillCreator.billPayloads(phoneNumber, "11", calls);
        assertBill(bill, phoneNumber, "11", 350.0, "rubbles");
    }

    @Test
    public void onlyInput100Min30SecCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "100 min 30 sec input call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff11BillCreator.billPayloads(phoneNumber, "11", calls);
        assertBill(bill, phoneNumber, "11", 0.0, "rubbles");
    }

    @Test
    public void onlyOutput100Min30SecCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "100 min 30 sec output call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff11BillCreator.billPayloads(phoneNumber, "11", calls);
        assertBill(bill, phoneNumber, "11", 52.0, "rubbles");
    }

    @Test
    public void updatedTariffInputCallsTest() {
        when(tariffService.findById(any())).thenReturn(updatedTariff);
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "100 min input call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff11BillCreator.billPayloads(phoneNumber, "11", calls);
        assertBill(bill, phoneNumber, "11", 25.0, updatedTariff.getMonetaryUnit());
    }

    @Test
    public void updatedTariffOutputCallsTest() {
        when(tariffService.findById(any())).thenReturn(updatedTariff);
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "100 min output call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff11BillCreator.billPayloads(phoneNumber, "11", calls);
        assertBill(bill, phoneNumber, "11", 175.0, updatedTariff.getMonetaryUnit());
    }

    @Test
    public void updatedTariff300Min59SecInputCallsTest() {
        when(tariffService.findById(any())).thenReturn(updatedTariff);
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "300 min 59 sec input call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff11BillCreator.billPayloads(phoneNumber, "11", calls);
        assertBill(bill, phoneNumber, "11", 25.0, updatedTariff.getMonetaryUnit());
    }

    @Test
    public void updatedTariff300Min1SecOutputCallsTest() {
        when(tariffService.findById(any())).thenReturn(updatedTariff);
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "300 min 1 sec output call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff11BillCreator.billPayloads(phoneNumber, "11", calls);
        assertBill(bill, phoneNumber, "11", 528.5, updatedTariff.getMonetaryUnit());
    }

    private void assertBill(BillEntity bill, long expPhone, String expTariffId, double expSum, String expMonetaryUnit) {
        assertEquals(expPhone, bill.phoneNumber());
        assertEquals(expTariffId, bill.tariffId());
        assertEquals(expSum, bill.totalSum());
        assertEquals(expMonetaryUnit, bill.monetaryUnit());
    }

    private List<CallInformation> getCallsFromFile(String filePath) {
        List<CallInformation> calls = new ArrayList<>();
        try(Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                CallInformation call = CallInformationParser.parseCallInformation(line.replace(" ","").split(","));
                calls.add(call);
            }
        } catch (Exception e) {
            log.error("File not found at path or not valid {}", filePath);
        }
        return calls;
    }
}
