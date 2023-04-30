package org.jenjetsu.com.brt;

import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.entity.Tariff;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase(replace =  AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {AbonentRepository.class})
@EnableJpaRepositories(basePackages = {"org.jenjetsu.com.core.*"})
@EntityScan("org.jenjetsu.com.core.entity")
public class AbonentRepositoryTest {

    @Autowired
    private AbonentRepository abonentRep;

    private Tariff abonentTariff;
    private Tariff negativeAbonentTariff = new Tariff("11", 0, 0, 0, null, null);
    private Abonent negativeAbonent = new Abonent(null, 88005553535l, -100, negativeAbonentTariff, null);
    private Abonent abonent;


    @BeforeEach
    void init() {
        abonentTariff = new Tariff("06", 0, 0, 0, null, null);
        abonent = new Abonent(null, 79347192857l, 200, abonentTariff, null);
    }

    @Test
    public void createTest() {
        assertDoesNotThrow(() -> abonentRep.saveAndFlush(abonent));
        assertNotNull(abonent.getId());
    }

    @Test
    public void selectOneTest() {
        abonentRep.save(abonent);
        Optional<Abonent> optionalAbonent = abonentRep.findByPhoneNumber(abonent.getPhoneNumber());
        assertTrue(optionalAbonent.isPresent());
        assertEquals(abonent.getId(), optionalAbonent.get().getId());
    }

    @Test
    public void deleteOneTest() {
        abonentRep.save(abonent);
        abonentRep.deleteByPhoneNumber(abonent.getPhoneNumber());
        Optional<Abonent> optionalAbonent = abonentRep.findByPhoneNumber(abonent.getPhoneNumber());
        assertFalse(optionalAbonent.isPresent());
    }

    @Test
    public void deleteAllWithNegativeBalance() {
        abonentRep.save(abonent);
        abonentRep.save(negativeAbonent);
        abonentRep.deleteByBalanceLessThan(0);
        abonentRep.findAll().forEach(abonent -> assertTrue(abonent.getBalance() >= 0));
    }

    @Test
    public void updateTest() {
        abonentRep.save(abonent);
        abonent.setTariff(negativeAbonentTariff);
        abonentRep.save(abonent);
        Optional<Abonent> optionalAbonent = abonentRep.findByPhoneNumber(abonent.getPhoneNumber());
        assertTrue(optionalAbonent.isPresent());
        Abonent updatedAbonent = optionalAbonent.get();
        assertEquals(updatedAbonent.getTariff().getId(), abonent.getTariff().getId());
    }
}
