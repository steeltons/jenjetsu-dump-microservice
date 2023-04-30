package org.jenjetsu.com.brt;

import org.jenjetsu.com.brt.logic.BillFileParser;
import org.jenjetsu.com.core.entity.BillEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BillFileParserTest {

    @Spy
    private BillFileParser fileParser;
    private Resource billFile;
    private Resource notValidBill;
    private Resource nullResource = null;

    @BeforeEach
    void init() {
        billFile = new FileSystemResource("src/test/resources/bill file test.bill");
        notValidBill = new FileSystemResource("src/test/resources/not valid bill file test.bill");
    }

    @Test
    public void workingTest() {
        assertTrue(billFile.exists());
        Collection<BillEntity> entities = fileParser.parseBillFileToBillEntities(billFile);
        assertFalse(entities.isEmpty());
        entities.forEach(this::validateBillEntity);
    }

    @Test
    public void testWithInvalidBillWithStartCalling() {
        assertThrows(Exception.class, () -> fileParser.parseBillFileToBillEntities(notValidBill));
    }

    @Test
    public void testWithNullResource() {
        assertThrows(Exception.class, () -> fileParser.parseBillFileToBillEntities(nullResource));
    }

    @Test
    public void testWithInvalidBillByCost() {
        notValidBill = new FileSystemResource("src/test/resources/not valid bill file test2.bill");
        assertThrows(Exception.class, () -> fileParser.parseBillFileToBillEntities(notValidBill));
    }

    @Test
    public void testWithInvalidBillByCallType() {
        notValidBill = new FileSystemResource("src/test/resources/not valid bill file test3.bill");
        assertThrows(Exception.class, () -> fileParser.parseBillFileToBillEntities(notValidBill));
    }

    private void validateBillEntity(BillEntity billEntity) {
        assertNotNull(billEntity.phoneNumber());
        assertNotNull(billEntity.tariffId());
        assertTrue(billEntity.totalSum() >= 0);
        assertNotNull(billEntity.monetaryUnit());
        billEntity.dtoList().forEach(abonentPayload -> {
            assertTrue(abonentPayload.getCallType() == 1 || abonentPayload.getCallType() == 2);
            assertNotNull(abonentPayload.getStartTime());
            assertNotNull(abonentPayload.getEndTime());
            assertNotNull(abonentPayload.getDuration());
            assertTrue(abonentPayload.getStartTime().compareTo(abonentPayload.getEndTime()) < 0);
            assertTrue(abonentPayload.getCost() >= 0);
        });
    }
}
