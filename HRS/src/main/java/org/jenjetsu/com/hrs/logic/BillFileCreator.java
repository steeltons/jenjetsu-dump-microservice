package org.jenjetsu.com.hrs.logic;

import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.hrs.logic.entity.CdrPlusEntity;
import org.jenjetsu.com.hrs.logic.tariffCalculator.TariffBillsCreator;
import org.jenjetsu.com.hrs.util.AbonentPayloadParser;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <h2>Cdr plus manipulator</h2>
 * Class that make operations on cdr plus file like parse it, bill and
 * write to bill file
 */
@Service
public class BillFileCreator {

    private final ApplicationContext applicationContext;
    private final BillFileWriter billFileWriter;
    private final CdrPlusFileParser cdrPlusFileParser;

    public BillFileCreator(ApplicationContext applicationContext,
                           BillFileWriter billFileWriter,
                           CdrPlusFileParser cdrPlusFileParser) {
        this.applicationContext = applicationContext;
        this.billFileWriter = billFileWriter;
        this.cdrPlusFileParser = cdrPlusFileParser;
    }

    /**
     * <h2>Create bill file</h2>
     * Method that create bill file for Abonent from cdr plus file
     * @param cdrPlusFile
     * @return
     */
    public Resource createBillFile(MultipartFile cdrPlusFile) {
        CdrPlusEntity cdrPlusEntity = cdrPlusFileParser.parseFile(cdrPlusFile);
        TariffBillsCreator tariffBillsCreator = getTariffBiller(cdrPlusEntity.getTariffId());
        List<CallInformation> sortedCalls =
                cdrPlusEntity.getCalls().stream()
                        .sorted((c1, c2) -> c1.getStartCallingTime().compareTo(c2.getStartCallingTime()))
                        .collect(Collectors.toList());

        List<AbonentPayload> filledPayloads = tariffBillsCreator.billPayloads(cdrPlusEntity.getTariffId(), sortedCalls);

        String tariffId = cdrPlusEntity.getTariffId();
        Long phoneNumber = cdrPlusEntity.getPhoneNumber();
        Resource billFile = billFileWriter.writeToFile(phoneNumber, tariffId, filledPayloads);
        return billFile;
    }

    private TariffBillsCreator getTariffBiller(String tariffId) {
        return applicationContext.getBean("tariff"+tariffId+"BillCreator", TariffBillsCreator.class);
    }
}
