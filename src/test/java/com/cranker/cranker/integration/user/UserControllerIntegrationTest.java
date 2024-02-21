package com.cranker.cranker.integration.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldReturnUserInfo() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("user user"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"))
                .andExpect(jsonPath("$.isVerified").value(false));
    }

    @Test
    @WithMockUser(username = "user2@gmail.com", roles = "USER")
    void shouldReturnNotFoundWhenProvidedInvalidUser() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldReturnForbiddenWhenNoUserProvided() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }
}
