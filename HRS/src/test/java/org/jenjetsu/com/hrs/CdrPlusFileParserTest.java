package org.jenjetsu.com.hrs;

import lombok.SneakyThrows;
import org.jenjetsu.com.hrs.logic.CdrPlusFileParser;
import org.jenjetsu.com.hrs.logic.entity.CdrPlusEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CdrPlusFileParserTest {

    @Spy
    private CdrPlusFileParser cdrPlusFileParser;

    @Test
    public void testInit() {
        assertNotNull(cdrPlusFileParser);
    }

    @Test
    public void parseNormalFileTest() {
        Collection<CdrPlusEntity> entities = null;
        try {
            MultipartFile file = readFileByFilePath("src/test/resources/cdr plus file tests/goodTest.cdrPlus");
             entities = cdrPlusFileParser.parseCdrPlusFile(file);
        } catch (Exception e) {
            entities = new ArrayList<>();
        }
        assertTrue(areCdrPlusEntitiesUnique(entities));
        assertNotEquals(0, entities.size());
    }

    @Test
    @SneakyThrows
    public void parseInvalidFileTest() {
        MultipartFile file = readFileByFilePath("src/test/resources/cdr plus file tests/badTest.cdrPlus");
        assertThrows(Exception.class, () -> cdrPlusFileParser.parseCdrPlusFile(file));
    }

    @Test
    public void nullFileTest() {
        assertThrows(Exception.class, () -> cdrPlusFileParser.parseCdrPlusFile(null));
    }

    @Test
    public void emptyFileTest() {
        MultipartFile file = new MockMultipartFile("test.txt", new byte[0]);
        assertThrows(Exception.class, () -> cdrPlusFileParser.parseCdrPlusFile(file));
    }

    @Test
    @SneakyThrows
    public void noPhoneCallsTest() {
        MultipartFile file = readFileByFilePath("src/test/resources/cdr plus file tests/phonesWithoutCallsFileTest.cdrPlus");
        assertThrows(Exception.class, () -> cdrPlusFileParser.parseCdrPlusFile(file));
    }

    private MultipartFile readFileByFilePath(String filePath) throws IOException {
        return new MockMultipartFile("test.cdrPlus",new FileInputStream(filePath));
    }
    private boolean areCdrPlusEntitiesUnique(Collection<CdrPlusEntity> entities) {
        List<Long> uniquePhoneNumbers = new ArrayList<>();
        entities.stream().takeWhile(cdrPlusEntity -> {
            if(!uniquePhoneNumbers.contains(cdrPlusEntity.getPhoneNumber())) {
                uniquePhoneNumbers.add(cdrPlusEntity.getPhoneNumber());
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return entities.size() == uniquePhoneNumbers.size();
    }

}
