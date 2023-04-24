package org.jenjetsu.com.brt.logic;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.dto.AbonentBillingDto;
import org.jenjetsu.com.core.dto.BillingDto;
import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.service.AbonentPayloadService;
import org.jenjetsu.com.core.service.AbonentService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <h2>Billing process</h2>
 * Class which start billing process and ends it.
 */
@Service
@Slf4j
public class BillingProcess {

    private final FileMainpulator fileMainpulator;
    private final CdrFileGetter getter;
    private final CdrFileSender sender;
    private final AbonentService abonentService;
    private final PhoneCallGrouper phoneCallGrouper;
    private final AbonentValidator abonentValidator;
    private final AbonentPayloadService abonentPayloadService;

    public BillingProcess(FileMainpulator fileMainpulator, CdrFileGetter getter,
                          CdrFileSender sender, AbonentService abonentService,
                          PhoneCallGrouper phoneCallGrouper, AbonentValidator abonentValidator,
                          AbonentPayloadService abonentPayloadService) {
        this.fileMainpulator = fileMainpulator;
        this.getter = getter;
        this.sender = sender;
        this.abonentService = abonentService;
        this.phoneCallGrouper = phoneCallGrouper;
        this.abonentValidator = abonentValidator;
        this.abonentPayloadService = abonentPayloadService;
    }

    /**
     * <h2>Bill Abonents</h2>
     * Method that start and ends to bill abonents
     * @return
     */
    @SneakyThrows
    public BillingDto billAbonents() {
        log.info("Start billing");
        abonentService.authorizeAbonents(); // Удалаем из бд пользователей с минимальным балансом
        abonentPayloadService.deleteAll(); // Удалаем звонки из бд
        Resource cdr = getter.getCdrFile(); // Получаем файл с CDR
        Map<Long, List<String>> phoneCallsMap = phoneCallGrouper.groupPhoneCallsByPhone(cdr); // Парсим и группируем по номеру телефона
        List<Resource> phoneCallsFile = fileMainpulator.createPhoneCallsFileList(phoneCallsMap); // Записываем звонки в CDR+
        for (Resource resource : phoneCallsFile) {
            Resource bill = sender.getBillFileFromHRS(resource); // Отправляем в HRS и получаем ответ
            List<AbonentPayload> payloads = fileMainpulator.parsePayloadsFromFile(bill); // Парсим в Звонки
            abonentValidator.validateAbonent(payloads); // Проверяем на минусовой баланс
        }
        List<Abonent> abonents = abonentService.findAll(); // Собираем оставшихся абонентов
        List<AbonentBillingDto> abonentBillingDtoList = abonents.stream()
                .map(a -> new AbonentBillingDto(a.getPhoneNumber(), a.getBalance()))
                .collect(Collectors.toList());
        log.info("End billing");
        return new BillingDto(abonentBillingDtoList);
    }
}
