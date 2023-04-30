package org.jenjetsu.com.hrs;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.BillEntity;
import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.core.entity.Tariff;
import org.jenjetsu.com.core.service.TariffService;
import org.jenjetsu.com.core.util.CallInformationParser;
import org.jenjetsu.com.hrs.logic.tariffCalculator.Tariff03BillCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class Tariff03Test {

    @Mock
    private TariffService tariffService;
    @InjectMocks
    private Tariff03BillCreator tariff03BillCreator;

    private Tariff nativeTariff = new Tariff("03", 0, 1.5, 1.5, null, "rubbles");
    private Tariff updatedTariff = new Tariff("03", 50, 0.5, 1.2, null, "krone");

    @BeforeEach
    void init() {
        when(tariffService.findById(any())).thenReturn(nativeTariff);
        tariff03BillCreator = new Tariff03BillCreator(tariffService);
    }

    @Test
    public void onlyInput100MinCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "100 min input call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff03BillCreator.billPayloads(phoneNumber, "03", calls);
        assertBill(bill, phoneNumber, "03", 150.0, "rubbles");
    }

    @Test
    public void onlyOutput100MinCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "100 min output call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff03BillCreator.billPayloads(phoneNumber, "03", calls);
        assertBill(bill, phoneNumber, "03", 150.0, "rubbles");
    }

    @Test
    public void onlyInput300MinCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "300 min input call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff03BillCreator.billPayloads(phoneNumber, "03", calls);
        assertBill(bill, phoneNumber, "03", 450.0, "rubbles");
    }

    @Test
    public void onlyOutput300MinCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "300 min output call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff03BillCreator.billPayloads(phoneNumber, "03", calls);
        assertBill(bill, phoneNumber, "03", 450.0, "rubbles");
    }

    @Test
    public void randomCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "random call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff03BillCreator.billPayloads(phoneNumber, "03", calls);
        assertBill(bill, phoneNumber, "03", 1084.5, "rubbles");
    }

    @Test
    public void tariffChangeInputCallsTest() {
        when(tariffService.findById(any())).thenReturn(updatedTariff);
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "100 min input call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff03BillCreator.billPayloads(phoneNumber, "03", calls);
        assertBill(bill, phoneNumber, "03", 100.0, updatedTariff.getMonetaryUnit());
    }

    @Test
    public void tariffChangeOutputCallsTest() {
        when(tariffService.findById(any())).thenReturn(updatedTariff);
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "100 min output call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff03BillCreator.billPayloads(phoneNumber, "03", calls);
        assertBill(bill, phoneNumber, "03", 170.0, updatedTariff.getMonetaryUnit());
    }

    @Test
    public void tariffChangeRandomCallsTest() {
        when(tariffService.findById(any())).thenReturn(updatedTariff);
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "random call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff03BillCreator.billPayloads(phoneNumber, "03", calls);
        assertBill(bill, phoneNumber, "03", 643.2, updatedTariff.getMonetaryUnit());
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
