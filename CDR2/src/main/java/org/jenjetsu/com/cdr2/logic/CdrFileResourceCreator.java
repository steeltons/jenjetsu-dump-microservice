package org.jenjetsu.com.cdr2.logic;

import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.entity.CallInformation;
import org.jenjetsu.com.core.exception.CdrCreateException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

/**
 * <h2>Cdr file resource creator</h2>
 * Class which write calls to ByteArrayResource
 */
@Service
@Slf4j
public class CdrFileResourceCreator {

    /**
     * <h2>Create resource from calls</h2>
     * @param calls - collection of calls
     * @return resource - cdr byte addy resource
     * @throws CdrCreateException
     */
    public Resource createResourceFromCalls(Collection<CallInformation> calls) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Iterator<CallInformation> iter = calls.iterator();
            while (iter.hasNext()) {
                String line = iter.next().toString() + ((iter.hasNext()) ? "\n" : "");
                out.write(line.getBytes(StandardCharsets.UTF_8));
            }
            out.close();
            ByteArrayResource resource = new ByteArrayResource(out.toByteArray()) {
                @Override
                public String getFilename() {
                    return UUID.randomUUID().toString()+".cdr";
                }
            };
            log.info("CdrFileResourceCreator: write resource with filename {}", resource.getFilename());
            return resource;
        } catch (Exception e) {
            log.error("CdrFileResourceCreator: IMPOSSIBLE TO CREATE RESOURCE. Error message {}", e.getMessage());
            throw new CdrCreateException(String.format("Impossible to create cdr file. Error message: %s", e.getMessage()));
        }
    }
}
