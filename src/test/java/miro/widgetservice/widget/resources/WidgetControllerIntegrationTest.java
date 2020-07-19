package miro.widgetservice.widget.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import miro.widgetservice.widget.domain.Widget;
import miro.widgetservice.widget.domain.WidgetRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.*;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private WidgetRepository repository;

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

    @Test
    public void shouldRetrieveWidgetById() throws Exception {
        var widget = repository.save(Widget.builder()
                .coordinates(new Point(0, 0))
                .width(100)
                .height(100)
                .zIndex(1)
            .build());

        repository.save(Widget.builder()
                .coordinates(new Point(10, 10))
                .width(200)
                .height(200)
                .zIndex(2)
                .build());

        mockMvc.perform(
                get("/widget/{id}", widget.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.modified_at").exists())
                .andExpect(jsonPath("$.coordinates.x").value(0))
                .andExpect(jsonPath("$.coordinates.y").value(0))
                .andExpect(jsonPath("$.width").value(100))
                .andExpect(jsonPath("$.height").value(100))
                .andExpect(jsonPath("$.z_index").value(1));
    }

    @Test
    public void shouldReturn404WhenWidgetByIdNotFound() throws Exception {
        mockMvc.perform(
                get("/widget/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void shouldDeleteWidgetById() throws Exception {
        var widget = repository.save(Widget.builder()
                .coordinates(new Point(0, 0))
                .width(100)
                .height(100)
                .zIndex(1)
                .build());

        mockMvc.perform(
                delete("/widget/{id}", widget.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(
                delete("/widget/{id}", widget.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}