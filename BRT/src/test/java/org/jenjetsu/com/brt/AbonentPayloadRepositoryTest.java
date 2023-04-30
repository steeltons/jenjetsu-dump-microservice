package org.jenjetsu.com.brt;

import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.Tariff;
import org.jenjetsu.com.core.repository.AbonentPayloadRepository;
import org.jenjetsu.com.core.repository.AbonentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase(replace =  AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {AbonentPayloadRepository.class, AbonentRepository.class})
@EnableJpaRepositories(basePackages = {"org.jenjetsu.com.core.*"})
@EntityScan("org.jenjetsu.com.core.entity")
public class AbonentPayloadRepositoryTest {

    @Autowired
    private AbonentPayloadRepository payloadRep;
    @Autowired
    private AbonentRepository abonentRepository;

    private AbonentPayload abonentPayload;
    private AbonentPayload secondAbonentPayload;
    private AbonentPayload notValidPayload;
    private Abonent abonent;

    @BeforeEach
    void init() {
        Tariff tariff = new Tariff();
        tariff.setId("06");
        abonent = new Abonent(null, 88005553535l, 100, tariff, null);
        abonentPayload = new AbonentPayload(null, abonent, (byte) 1, new Timestamp(System.currentTimeMillis()),
                                            new Timestamp(System.currentTimeMillis() + 2000l), Time.valueOf("00:02:00"), 12.0);
        secondAbonentPayload = new AbonentPayload(null, abonent, (byte) 2, new Timestamp(System.currentTimeMillis() + 3000l),
                                            new Timestamp(System.currentTimeMillis() + 6000l), Time.valueOf("00:03:00"), 15);
        notValidPayload = new AbonentPayload(null, abonent, (byte) 1, new Timestamp(System.currentTimeMillis()),
                                            new Timestamp(System.currentTimeMillis() - 5000l), Time.valueOf("00:05:00"), -15);
        abonentRepository.save(abonent);
    }
    @Test
    public void createTest() {
        payloadRep.save(abonentPayload);
        assertNotNull(abonentPayload.getPayloadId());
    }

    @Test
    public void selectOneTest() {
        payloadRep.save(abonentPayload);
        Optional<AbonentPayload> optionalAbonentPayload = payloadRep.findById(abonentPayload.getPayloadId());
        assertTrue(optionalAbonentPayload.isPresent());
        AbonentPayload selectedPayload = optionalAbonentPayload.get();
        assertEquals(selectedPayload.getPayloadId(), abonentPayload.getPayloadId());
    }

    @Test
    public void selectAllByAbonentId() {
        payloadRep.save(abonentPayload);
        payloadRep.save(secondAbonentPayload);
        List<AbonentPayload> payloads = payloadRep.findAbonentPayloadByAbonentPhoneNumber(abonent.getPhoneNumber());
        assertEquals(2, payloads.size());
    }

    @Test
    public void invalidPayloadTest() {
        assertThrows(Exception.class, () -> payloadRep.save(notValidPayload));
    }

    @Test
    public void deleteAllTest() {
        payloadRep.save(abonentPayload);
        payloadRep.save(secondAbonentPayload);
        payloadRep.deleteAll();
        List<AbonentPayload> payloads = payloadRep.findAll();
        assertTrue(payloads.isEmpty());
    }

}
