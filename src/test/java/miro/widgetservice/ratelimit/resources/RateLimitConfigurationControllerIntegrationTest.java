package miro.widgetservice.ratelimit.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RateLimitConfigurationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void shouldGetAllConfigs() throws Exception {
        mockMvc.perform(get("/admin/rate-limit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.configured_by == \"DEFAULT\")].handler_identity").value("*"))
                .andExpect(jsonPath("$.[?(@.configured_by == \"DEFAULT\")].allowed_requests").value(1000))
                .andExpect(jsonPath("$.[?(@.configured_by == \"DEFAULT\")].time_window_in_seconds").value(60))
                .andExpect(jsonPath("$.[?(@.configured_by == \"DEFINED_BY_CLASS\")].handler_identity").value("WidgetController"))
                .andExpect(jsonPath("$.[?(@.configured_by == \"DEFINED_BY_CLASS\")].allowed_requests").value(1000))
                .andExpect(jsonPath("$.[?(@.configured_by == \"DEFINED_BY_CLASS\")].time_window_in_seconds").value(60))
                .andExpect(jsonPath("$.[?(@.configured_by == \"DEFINED_BY_METHOD\")].handler_identity").value("getAllWidgets"))
                .andExpect(jsonPath("$.[?(@.configured_by == \"DEFINED_BY_METHOD\")].allowed_requests").value(200))
                .andExpect(jsonPath("$.[?(@.configured_by == \"DEFINED_BY_METHOD\")].time_window_in_seconds").value(60));
    }

    @Test
    public void shouldOverrideDefaultRateLimitConfig() throws Exception {
        var request = RateLimitUpdateRequest.builder()
                .handlerIdentity("*")
                .allowedRequests(2000)
                .timeWindowInSeconds(200)
                .build();

        mockMvc.perform(
                post("/admin/rate-limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.configured_by == \"DEFAULT\")].handler_identity").value("*"))
                .andExpect(jsonPath("$.[?(@.configured_by == \"DEFAULT\")].allowed_requests").value(1000))
                .andExpect(jsonPath("$.[?(@.configured_by == \"DEFAULT\")].time_window_in_seconds").value(60))
                .andExpect(jsonPath("$.[?(@.configured_by == \"OVERRIDDEN_BY_ADMIN\")].handler_identity").value("*"))
                .andExpect(jsonPath("$.[?(@.configured_by == \"OVERRIDDEN_BY_ADMIN\")].allowed_requests").value(2000))
                .andExpect(jsonPath("$.[?(@.configured_by == \"OVERRIDDEN_BY_ADMIN\")].time_window_in_seconds").value(200));
    }
}