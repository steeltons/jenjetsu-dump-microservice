package org.jenjetsu.com.hrs;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.*;
import org.jenjetsu.com.core.service.TariffService;
import org.jenjetsu.com.hrs.logic.tariffCalculator.Tariff06BillCreator;
import org.jenjetsu.com.core.util.CallInformationParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private TariffOption nativeTariffOption = new TariffOption(0l, null, 0, 0, 300, false);
    private Tariff nativeTariff = new Tariff("06", 100, 1, 1, Arrays.asList(nativeTariffOption), "rubbles");
    private TariffOption updatedTariffOptions = new TariffOption(0l, null, 0.2, 0.5, 50, false);
    private Tariff updatedTariff = new Tariff("06", 100, 2, 2.5, Arrays.asList(updatedTariffOptions), "krones");

    @BeforeEach
    public void init() {
        when(tariffService.findById(any())).thenReturn(nativeTariff);
        tariff06BillCreator = new Tariff06BillCreator(tariffService);
    }

    @Test
    public void onlyInput100MinCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "100 min input call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff06BillCreator.billPayloads(phoneNumber, "06", calls);
        assertBill(bill, phoneNumber, "06", 100.0, "rubbles");
    }

    @Test
    public void onlyOutput100MinCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "100 min output call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff06BillCreator.billPayloads(phoneNumber, "06", calls);
        assertBill(bill, phoneNumber, "06", 100.0, "rubbles");
    }

    @Test
    public void onlyInput300MinCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "300 min input call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff06BillCreator.billPayloads(phoneNumber, "06", calls);
        assertBill(bill, phoneNumber, "06", 100.0, "rubbles");
    }

    @Test
    public void onlyOutput300MinCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "300 min output call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff06BillCreator.billPayloads(phoneNumber, "06", calls);
        assertBill(bill, phoneNumber, "06", 100.0, "rubbles");
    }

    @Test
    public void onlyInput300Min59SecCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "300 min 59 sec input call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff06BillCreator.billPayloads(phoneNumber, "06", calls);
        assertBill(bill, phoneNumber, "06", 101.0, "rubbles");
    }

    @Test
    public void onlyOutput300Min1SecCallsTest() {
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "300 min 1 sec output call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff06BillCreator.billPayloads(phoneNumber, "06", calls);
        assertBill(bill, phoneNumber, "06", 101.0, "rubbles");
    }

    @Test
    public void updatedTariffOnlyInputTest() {
        when(tariffService.findById(any())).thenReturn(updatedTariff);
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "100 min input call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff06BillCreator.billPayloads(phoneNumber, "06", calls);
        assertBill(bill, phoneNumber, "06", 210.0, updatedTariff.getMonetaryUnit());
    }

    @Test
    public void updatedTariffOnlyOutputTest() {
        when(tariffService.findById(any())).thenReturn(updatedTariff);
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "100 min output call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff06BillCreator.billPayloads(phoneNumber, "06", calls);
        assertBill(bill, phoneNumber, "06", 250, updatedTariff.getMonetaryUnit());
    }

    @Test
    public void updatedTariff300MinInputCallsTest() {
        when(tariffService.findById(any())).thenReturn(updatedTariff);
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "300 min input call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff06BillCreator.billPayloads(phoneNumber, "06", calls);
        assertBill(bill, phoneNumber, "06", 610, updatedTariff.getMonetaryUnit());
    }

    @Test
    public void updatedTariff300MinOutputCallsTest() {
        when(tariffService.findById(any())).thenReturn(updatedTariff);
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "300 min output call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff06BillCreator.billPayloads(phoneNumber, "06", calls);
        assertBill(bill, phoneNumber, "06", 750, updatedTariff.getMonetaryUnit());
    }

    @Test
    public void updatedTariff300Min59SecInputCallsTest() {
        when(tariffService.findById(any())).thenReturn(updatedTariff);
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "300 min 59 sec input call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff06BillCreator.billPayloads(phoneNumber, "06", calls);
        assertBill(bill, phoneNumber, "06", 612.0, updatedTariff.getMonetaryUnit());
    }

    @Test
    public void updatedTariff300Min1SecOutputCallsTest() {
        when(tariffService.findById(any())).thenReturn(updatedTariff);
        List<CallInformation> calls = getCallsFromFile("src/test/resources/phone calls file tests/" +
                "300 min 1 sec output call file test.txt");
        Long phoneNumber = calls.get(0).getPhoneNumber();
        BillEntity bill = tariff06BillCreator.billPayloads(phoneNumber, "06", calls);
        assertBill(bill, phoneNumber, "06", 752.5, updatedTariff.getMonetaryUnit());
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
