package org.jenjetsu.com.brt.logic;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.dto.BillingDto;
import org.jenjetsu.com.core.dto.CdrDto;
import org.jenjetsu.com.core.entity.BillEntity;
import org.jenjetsu.com.core.exception.BillReadFileException;
import org.jenjetsu.com.core.exception.CdrPlusCreateException;
import org.jenjetsu.com.core.logic.AbonentBiller;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h2>Billing process</h2>
 * Class which start billing process and ends it.
 */
/**
 * <h2>Billing process</h2>
 * Class which start billing process and ends it.
 */
@Service
@Slf4j
public class BillingProcess {

    private final CdrPlusFileWriter cdrPlusFileWriter;
    private final CdrFileGetter getter;
    private final CdrFileSender sender;
    private final AbonentBiller biller;
    private final PhoneCallGrouper phoneCallGrouper;
    private final CdrDtoConverter cdrDtoConverter;
    private final BillFileParser billFileParser;

    public BillingProcess(CdrPlusFileWriter cdrPlusFileWriter,
                          CdrFileGetter getter,
                          CdrFileSender sender,
                          AbonentBiller biller,
                          PhoneCallGrouper phoneCallGrouper,
                          CdrDtoConverter cdrDtoConverter,
                          BillFileParser billFileParser) {
        this.cdrPlusFileWriter = cdrPlusFileWriter;
        this.getter = getter;
        this.sender = sender;
        this.biller = biller;
        this.phoneCallGrouper = phoneCallGrouper;
        this.cdrDtoConverter = cdrDtoConverter;
        this.billFileParser = billFileParser;
    }

    /**
     * <h2>Bill Abonents</h2>
     * Method that start and ends to bill abonents
     * @return BillingDto - information of billed abonents
     */
    public BillingDto billAbonents() {
        log.info("START BILLING");
        Resource cdrFile = getter.getCdrFilePath();
        Resource cdrPlusFile = createCdrPlusFile(cdrFile);

        log.info("SENDING CDR+ FILE TO HRS");
        Resource billFile = sender.getBillFileFromHRS(cdrPlusFile);

        BillingDto billingDto = getBillingDto(billFile);
        log.info("END BILLING");
        return billingDto;
    }

    /**
     * <h2>Create cdr plus file</h2>
     * Method which get cdr file from CDR and create cdr+ file
     * @param resource - cdr byte file
     * @return cdr+ byte file
     * @throws CdrPlusCreateException - impossible to parse cdr file or create cdr+ file
     */
    private Resource createCdrPlusFile(Resource resource) throws CdrPlusCreateException {
        Map<Long, List<String>> phoneCallsMap = phoneCallGrouper.groupPhoneCallsByPhone(resource);
        Collection<CdrDto> cdrDtos = cdrDtoConverter.convertCallsMapToCdrs(phoneCallsMap);
        Resource returnResource = cdrPlusFileWriter.writeCdrDtosToResource(cdrDtos);
        cdrPlusFileWriter.writeCdrResourceToDisk(returnResource);
        return returnResource;
    }

    /**
     * <h2>Get billing dto</h2>
     * Method which parse bill file, check abonent balance and collect billed abonents to BillingDto
     * @param billFile - bill byte file
     * @return BillingDto - information of billed abonents
     * @throws BillReadFileException - impossible to parse bill file
     */
    private BillingDto getBillingDto(Resource billFile) throws BillReadFileException {
        Collection<BillEntity> billEntities = billFileParser.parseBillFileToBillEntities(billFile);
        BillingDto billingDto = biller.billAbonents(billEntities);
        return billingDto;
    }

}
