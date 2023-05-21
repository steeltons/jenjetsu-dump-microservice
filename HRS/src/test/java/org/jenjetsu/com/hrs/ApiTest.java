package org.jenjetsu.com.hrs;

import org.jenjetsu.com.core.exception.BillFileCreateException;
import org.jenjetsu.com.hrs.logic.BillFileManipulator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@TestPropertySource("classpath:test.properties")
@AutoConfigureMockMvc(addFilters = false)
public class ApiTest {

    @MockBean
    private BillFileManipulator fileCreator;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ResourceLoader loader;

    @Test
    public void initTest() {
        assertNotNull(mockMvc);
    }

    @Test
    public void apiWorkingTest() throws Exception {
        Resource resource = readFileAsResourseByFilePath("cdr plus file tests/goodTest.cdrPlus");
        MockMultipartFile file = new MockMultipartFile("file", resource.getInputStream());
        mockMvc.perform(multipart("/api/v1/billing/bill-number")
                        .file(file))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.MULTIPART_FORM_DATA));
    }

    @Test
    public void handleExceptionTest() throws Exception{
        Resource resource = readFileAsResourseByFilePath("cdr plus file tests/goodTest.cdrPlus");
        when(fileCreator.createBillFile(any())).thenThrow(new BillFileCreateException("impossible to create bill file"));
        MockMultipartFile file = new MockMultipartFile("file", resource.getInputStream());
        mockMvc.perform(multipart("/api/v1/billing/bill-number")
                        .file(file))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Impossible to create bill file"));
    }

    private Resource readFileAsResourseByFilePath(String filePath) {
        return loader.getResource("classpath:"+filePath);
    }
}
