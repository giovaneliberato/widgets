package miro.widgetservice.widget.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WidgetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void shouldCreateWidget() throws Exception {
        var request = WidgetCreationRequest.builder()
                .coordinates(new Point(0, 0))
                .width(100)
                .height(100)
                .zIndex(1)
                .build();

        mockMvc.perform(
                post("/widget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.modified_at").exists())
                .andExpect(jsonPath("$.coordinates.x").value(0))
                .andExpect(jsonPath("$.coordinates.y").value(0))
                .andExpect(jsonPath("$.width").value(100))
                .andExpect(jsonPath("$.height").value(100))
                .andExpect(jsonPath("$.z_index").value(1));
    }
}