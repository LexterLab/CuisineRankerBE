package com.cranker.cranker.integration.cloud;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CloudFileControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithBadRequestStatusWhenProvidedUnsupportedExtensionOnUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain",
                "test".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/cloud/files")
                .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithBadRequestWhenProvidedEmptyFileName() throws Exception {
        MockMultipartFile file = new MockMultipartFile("name", ".txt", "text/plain",
                "test".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/cloud/files")
                        .file(file))
                        .andExpect(status().isBadRequest());
    }
}
