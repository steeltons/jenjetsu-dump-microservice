package org.jenjetsu.com.hrs.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.BillEntity;
import org.jenjetsu.com.core.service.S3Service;
import org.jenjetsu.com.hrs.broker.sender.BrtMessageSender;
import org.jenjetsu.com.hrs.logic.entity.CdrPlusEntity;
import org.jenjetsu.com.hrs.logic.tariffCalculator.TariffBillsCreator;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2>Bill file manipulator</h2>
 * Create bill file from cdr+ file with billing phone numbers. Save it in S3 storage and send bill file to BRT.
 */
@Service
@AllArgsConstructor
@Slf4j
public class BillFileManipulator {

    private final ApplicationContext applicationContext;
    private final BillFileWriter billFileWriter;
    private final CdrPlusFileParser cdrPlusFileParser;
    private final S3Service miniService;
    private final BrtMessageSender brtMessageSender;

    /**
     * <h3>Create bill file</h3>
     * Method which create bill file
     * @param cdrPlusFilename - cdr+ filename in s3 storage
     * @return billFile - binary file
     */
    public Resource createBillFile(String cdrPlusFilename) {
        try {
            Resource cdrPlusFile = miniService.getObject(cdrPlusFilename);
            miniService.removeObject(cdrPlusFilename);
            List<CdrPlusEntity> cdrPlusEntities = cdrPlusFileParser.parseCdrPlusFile(cdrPlusFile);
            List<BillEntity> billEntities = new ArrayList<>();
            cdrPlusEntities.forEach(cdrEntity -> {
                TariffBillsCreator biller = getTariffBiller(cdrEntity.getTariffId());
                cdrEntity.getCalls().sort((c1, c2) -> c1.getStartCallingTime().compareTo(c2.getStartCallingTime()));
                BillEntity entity = biller.billPayloads(cdrEntity.getPhoneNumber(), cdrEntity.getTariffId(), cdrEntity.getCalls());
                billEntities.add(entity);
            });
            Resource billFile = billFileWriter.writeBillToResource(billEntities);
            return billFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <h2>Store bill file</h2>
     * Accept bile file as resource and save it in S3 storage
     * @param billFile - binary bill file
     * @return billFilename - new filename at S3 storage
     */
    public String storeBillFile(Resource billFile) {
        try {
            String billFilename = miniService.putObject(billFile.getFilename(), billFile.getInputStream());
            log.info("BillFileManipulator: store bill file {} in s3 storage", billFilename);
            return billFilename;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <h2>Send bill file to BRT</h2>
     * Send bill file filename to BRT using RabbitMq broker messenger
     * @param billFilename
     */
    public void sendBillFileToBrt(String billFilename) {
        brtMessageSender.sendBillFilenameToBrt(billFilename);
    }

    private TariffBillsCreator getTariffBiller(String tariffId) {
        return applicationContext.getBean("tariff"+tariffId+"BillCreator", TariffBillsCreator.class);
    }
}
