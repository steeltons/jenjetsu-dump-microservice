package org.jenjetsu.com.cdr2;

import org.jenjetsu.com.cdr2.logic.AbonentGenerator;
import org.jenjetsu.com.cdr2.logic.CdrFileManipulator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@TestPropertySource(locations = "classpath:testing.properties")
@AutoConfigureMockMvc(addFilters = false)
public class ApiTest {

    @MockBean
    private CdrFileManipulator cdrFileManipulator;
    @MockBean
    private AbonentGenerator abonentGenerator;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void initializationTest() {
        assertNotNull(mockMvc);
    }

    @Test
    public void apiWorkingTest() throws Exception {
       mockMvc.perform(get("http://localhost:8700/api/v1/get-calls"))
               .andExpect(content().contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().is2xxSuccessful());
       int i = 43;
    }

//    @Test
//    public void exceptionHandleTest() throws Exception{
//        when(cdrCreator.createCdrFile()).thenThrow(new CdrCreateException("test"));
//        mockMvc.perform(get("http://localhost:8700/api/v1/get-calls"))
//                .andExpect(status().is5xxServerError())
//                .andExpect(content().string("Impossible to create cdr file"));
//    }
}
