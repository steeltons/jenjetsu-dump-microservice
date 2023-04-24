package org.jenjetsu.com.hrs.logic;

import lombok.SneakyThrows;
import org.jenjetsu.com.core.entity.AbonentPayload;
import org.jenjetsu.com.hrs.logic.entity.CdrPlusEntity;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

@Service
public class BillFileWriter {

    @SneakyThrows
    public ByteArrayResource writeToFile(Long phoneNumber, String tariffId, List<AbonentPayload> payloadList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write((phoneNumber+" "+ tariffId+"\n").getBytes(StandardCharsets.UTF_8));
        Iterator<AbonentPayload> iter = payloadList.listIterator();
        while (iter.hasNext()) {
            out.write((iter.next()+(iter.hasNext() ? "\n" : "")).getBytes(StandardCharsets.UTF_8));
        }
        out.close();
        return new ByteArrayResource(out.toByteArray()) { // add file name
            @Override
            public String getFilename() {
                return phoneNumber+".bill";
            }
        };
    }
}
