package org.jenjetsu.com.brt.logic;

import lombok.SneakyThrows;
import org.jenjetsu.com.brt.util.PayloadParser;
import org.jenjetsu.com.core.entity.Abonent;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.core.entity.Tariff;
import org.jenjetsu.com.core.service.AbonentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <h2>File manipulator</h2>
 * Class that created to
 * //TODO name file
 */
@Service
public class FileMainpulator {

    private final AbonentService abonentService;

    public FileMainpulator(AbonentService abonentService) {
        this.abonentService = abonentService;
    }

    /**
     * <h2>Create phone calls file list</h2>
     * Method for grouping phone numbers, tariff IDs and call lists into a separate files.
     * @param groupedPhoneCalls
     * @return List of ByteArrayResource - byte files
     */
    @SneakyThrows
    public List<Resource> createPhoneCallsFileList(Map<Long, List<String>> groupedPhoneCalls) throws IOException{
        List<Resource> outputs = new ArrayList<>();
        for (Map.Entry<Long, List<String>> pair : groupedPhoneCalls.entrySet()) {
            Abonent abonent = abonentService.findByPhoneNumber(pair.getKey());
            Tariff tariff = abonent.getTariff();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write((abonent.getPhoneNumber()+" "+tariff.getId()+"\n").getBytes(StandardCharsets.UTF_8));
            Iterator<String> iter = pair.getValue().iterator();
            while (iter.hasNext()) {
                out.write((iter.next() + (iter.hasNext() ? "\n" : "")).getBytes(StandardCharsets.UTF_8));
            }
            out.close();
            ByteArrayResource resource = new ByteArrayResource(out.toByteArray()) {
                @Override
                public String getFilename() {
                    return pair.getKey() + ".cdrplus";
                }
            };
            outputs.add(resource);
        }
        return outputs;
    }

    /**
     * <h2>Parse payloads from file</h2>
     * @param file
     * @return
     */
    public List<AbonentPayload> parsePayloadsFromFile(Resource file) throws IOException {
        Scanner scanner = new Scanner(file.getInputStream());
        Long phoneNumber = Long.parseLong(scanner.nextLine().split(" ")[0]);
        Abonent abonent = abonentService.findByPhoneNumber(phoneNumber);
        List<AbonentPayload> payloadList = new ArrayList<>();
        while (scanner.hasNext()) {
            AbonentPayload payload = PayloadParser.parseFromLine(scanner.nextLine());
            payload.setAbonent(abonent);
            payloadList.add(payload);
        }
        return payloadList;
    }
}
