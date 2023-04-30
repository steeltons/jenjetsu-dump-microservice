package org.jenjetsu.com.hrs.logic;

import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.BillEntity;
import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.hrs.logic.entity.CdrPlusEntity;
import org.jenjetsu.com.hrs.logic.tariffCalculator.TariffBillsCreator;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
     * <h3>Create bill file</h3>
     * Method which create bill file
     * @param cdrPlusFile - cdr+ byte file
     * @return billFilePath - path to bill file
     */
    public Resource createBillFile(MultipartFile cdrPlusFile) {
        List<CdrPlusEntity> cdrPlusEntities = cdrPlusFileParser.parseCdrPlusFile(cdrPlusFile);
        List<BillEntity> billEntities = new ArrayList<>();
        cdrPlusEntities.forEach(cdrEntity -> {
            TariffBillsCreator biller = getTariffBiller(cdrEntity.getTariffId());
            cdrEntity.getCalls().sort((c1, c2) -> c1.getStartCallingTime().compareTo(c2.getStartCallingTime()));
            BillEntity entity = biller.billPayloads(cdrEntity.getPhoneNumber(), cdrEntity.getTariffId(), cdrEntity.getCalls());
            billEntities.add(entity);
        });
        Resource billFile = billFileWriter.writeBillToResource(billEntities);
        billFileWriter.writeToFile(billFile);
        return billFile;
    }

    private TariffBillsCreator getTariffBiller(String tariffId) {
        return applicationContext.getBean("tariff"+tariffId+"BillCreator", TariffBillsCreator.class);
    }
}
