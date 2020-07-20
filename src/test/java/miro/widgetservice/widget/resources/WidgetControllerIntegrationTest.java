package miro.widgetservice.widget.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import miro.widgetservice.config.ApplicationConfig;
import miro.widgetservice.widget.domain.Widget;
import miro.widgetservice.widget.domain.WidgetRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
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
@ContextConfiguration(classes = ApplicationConfig.class)
public class WidgetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WidgetRepository repository;

    @Before
    public void setUp() throws Exception {
        repository.dropAllWidgets();
    }

    @Test
    public void shouldCreateWidget() throws Exception {
        var request = WidgetCreationRequest.builder()
                .coordinates(new Point(0, 0))
                .width(100)
                .height(100)
                .zIndex(1)
                .build();

        mockMvc.perform(
                post("/widgets")
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
                get("/widgets/{id}", widget.getId())
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
    public void shouldRetrieveAllWidgetsSortedByZIndex() throws Exception {
        var widget1 = repository.save(Widget.builder()
                .coordinates(new Point(0, 0))
                .width(100)
                .height(100)
                .zIndex(1)
                .build());

        var widget2 = repository.save(Widget.builder()
                .coordinates(new Point(10, 10))
                .width(200)
                .height(200)
                .zIndex(2)
                .build());

        mockMvc.perform(
                get("/widgets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(widget2.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(widget1.getId().toString()));
    }

    @Test
    public void shouldReturn404WhenWidgetByIdNotFound() throws Exception {
        mockMvc.perform(
                get("/widgets/{id}", UUID.randomUUID())
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
                delete("/widgets/{id}", widget.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(
                delete("/widgets/{id}", widget.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldUpdateWidgetById() throws Exception {
        var widget = repository.save(Widget.builder()
                .coordinates(new Point(0, 0))
                .width(100)
                .height(100)
                .zIndex(1)
                .build());

        var request = WidgetUpdateRequest.builder()
                .coordinates(new Point(100, 100))
                .width(200)
                .height(200)
                .zIndex(-1)
                .build();

        mockMvc.perform(
                patch("/widgets/{widgetId}", widget.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(widget.getId().toString()))
                .andExpect(jsonPath("$.modified_at").exists())
                .andExpect(jsonPath("$.coordinates.x").value(100))
                .andExpect(jsonPath("$.coordinates.y").value(100))
                .andExpect(jsonPath("$.width").value(200))
                .andExpect(jsonPath("$.height").value(200))
                .andExpect(jsonPath("$.z_index").value(-1));
    }
}