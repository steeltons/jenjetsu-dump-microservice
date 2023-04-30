package org.jenjetsu.com.brt;

import org.jenjetsu.com.core.entity.Tariff;
import org.jenjetsu.com.core.entity.TariffOption;
import org.jenjetsu.com.core.repository.TariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase(replace =  AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {TariffRepository.class})
@EnableJpaRepositories(basePackages = {"org.jenjetsu.com.core.*"})
@EntityScan("org.jenjetsu.com.core.entity")
public class TariffRepositoryTest {

    @Autowired
    private TariffRepository tariffRep;
    private Tariff tariff;
    private TariffOption tariffOption;
    private TariffOption newOption;

    @BeforeEach
    void init() {
        tariff = new Tariff("12", 0, 2, 2, new ArrayList<>(), "rubbles");
        tariffOption = new TariffOption(null, tariff, 0.5, 0.5, 200, false);
        newOption = new TariffOption(null, tariff, 1, 1, 100, false);
        tariff.getOptions().add(tariffOption);
    }

    @Test
    public void createTest() {
        assertDoesNotThrow(() -> tariffRep.save(tariff));
    }

    @Test
    public void createInvalidTariffTest() {
        tariff.setId(null);
        assertThrows(Exception.class, () -> tariffRep.save(tariff));
    }

    @Test
    public void selectOneTest() {
        tariffRep.save(tariff);
        Optional<Tariff> optionalTariff = tariffRep.findById(tariff.getId());
        assertTrue(optionalTariff.isPresent());
        Tariff selectedTariff = optionalTariff.get();
        assertEquals(selectedTariff.getOutputCost(), tariff.getOutputCost());
        assertEquals(selectedTariff.getInputCost(), tariff.getInputCost());
        assertEquals(selectedTariff.getBasicPrice(), tariff.getBasicPrice());
        assertEquals(selectedTariff.getMonetaryUnit(), tariff.getMonetaryUnit());
    }

    @Test
    public void createWithPersistOptionsTest() {
        tariffRep.save(tariff);
        Optional<Tariff> optionalTariff = tariffRep.findById(tariff.getId());
        assertTrue(optionalTariff.isPresent());
        optionalTariff.get().getOptions().forEach(option -> assertNotNull(option.getOptionId()));
    }

    @Test
    public void deleteTest() {
        tariffRep.save(tariff);
        tariffRep.deleteById(tariff.getId());
        Optional<Tariff> optionalTariff = tariffRep.findById(tariff.getId());
        assertFalse(optionalTariff.isPresent());
    }

    @Test
    public void updateTest() {
        tariffRep.save(tariff);
        tariff.setInputCost(1.5);
        tariff.setOutputCost(100);
        tariffRep.save(tariff);
        Optional<Tariff> optionalTariff = tariffRep.findById(tariff.getId());
        assertTrue(optionalTariff.isPresent());
        Tariff updatedTariff = optionalTariff.get();
        assertEquals(tariff.getInputCost(), updatedTariff.getInputCost());
        assertEquals(tariff.getOutputCost(), updatedTariff.getOutputCost());
    }

    private void assertTariffOptionEqual(TariffOption t1, TariffOption t2) {
        assertNotNull(t1);
        assertNotNull(t2);
        assertEquals(t1.getIncomingBufferCost(), t2.getIncomingBufferCost());
        assertEquals(t1.getOutcomingBufferCost(), t2.getOutcomingBufferCost());
        assertEquals(t1.getTariffDurationMinutes(), t2.getTariffDurationMinutes());
        assertEquals(t1.isFreeBetweenDifferentProviders(), t2.isFreeBetweenDifferentProviders());
        assertEquals(t1.getTariff().getId(), t2.getTariff().getId());
    }
}
