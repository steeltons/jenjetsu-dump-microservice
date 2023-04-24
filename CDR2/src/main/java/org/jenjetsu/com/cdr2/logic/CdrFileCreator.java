package org.jenjetsu.com.cdr2.logic;

import lombok.SneakyThrows;
import org.jenjetsu.com.core.entity.CallInformation;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * <h2>CDR File Creator</h2>
 * Class that create CDR file
 */
@Service
public class CdrFileCreator {

    private final PhoneNumberCreator phoneNumberCreator;
    private final CallInfoCreator callInfoCreator;

    public CdrFileCreator(PhoneNumberCreator phoneNumberCreator, CallInfoCreator callInfoCreator) {
        this.phoneNumberCreator = phoneNumberCreator;
        this.callInfoCreator = callInfoCreator;
    }

    /**
     * <h2>Write CDR file from map</h2>
     * Method which generate abonents call information
     * @return ByteArrayResource - byte file
     */
    @SneakyThrows
    public ByteArrayResource createCdrFile() {
        Set<Long> phoneNumbers = phoneNumberCreator.createSetOfPhoneNumbers();
        Set<CallInformation> calls = callInfoCreator.createSetOfCallInformation(phoneNumbers);
        ByteArrayResource resource = writeToFile(calls);
        return resource;
    }

    private ByteArrayResource writeToFile(Set<CallInformation> calls) throws IOException{
        Iterator<CallInformation> iter = calls.iterator();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (iter.hasNext()) {
            out.write((iter.next() + (iter.hasNext() ? "\n" : "")).getBytes(StandardCharsets.UTF_8));
        }
        out.close();
        ByteArrayResource resource = new ByteArrayResource(out.toByteArray()) {
            @Override
            public String getFilename() {
                return UUID.randomUUID().toString()+".txt";
            }
        };
        return resource;
    }
}
