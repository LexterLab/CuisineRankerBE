package com.cranker.cranker.integration.notification;

import com.cranker.cranker.integration.BaseIntegrationTest;
import com.cranker.cranker.notification.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    public void setup() {
        flyway.migrate();
    }

    @AfterEach
    public void tearDown() {
        flyway.clean();
    }


    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldDismissNotificationAndRespondWithNoContent() throws Exception {
        Long notificationId = 1L;

        Long numberOfNotificationBefore = notificationRepository.count();

        mockMvc.perform(delete("/api/v1/notifications/{notificationId}", notificationId))
                .andExpect(status().isNoContent());

        Long numberOfNotificationAfter = notificationRepository.count();

        assertTrue(numberOfNotificationBefore > numberOfNotificationAfter);
    }

    @Test
    void shouldRespondWithUnauthorizedWhenDismissingNotificationsWithoutAuthorization() throws Exception {
        Long notificationId = 1L;

        mockMvc.perform(delete("/api/v1/notifications/{notificationId}", notificationId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "unknown@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenUnknownUserTriesToDismissNotifications() throws Exception {
        Long notificationId = 1L;

        mockMvc.perform(delete("/api/v1/notifications/{notificationId}", notificationId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenDismissingUnknownNotification() throws Exception {
        Long notificationId = 0L;

        mockMvc.perform(delete("/api/v1/notifications/{notificationId}", notificationId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "usertwo@gmail.com", roles = "USER")
    void shouldRespondWithConflictWhenDismissingSomeoneElseNotification() throws Exception {
        Long notificationId = 1L;

        mockMvc.perform(delete("/api/v1/notifications/{notificationId}", notificationId))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithUserNotificationsAndOKStatus() throws Exception {

        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(notificationRepository.count()))
                .andExpect(jsonPath("$.notifications").isArray());
    }

    @Test
    void shouldRespondWithForbiddenWhenRetrievingNotificationsWithoutAuthorization() throws Exception {
        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isForbidden());
    }
}
