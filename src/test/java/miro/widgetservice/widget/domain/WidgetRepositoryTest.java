package miro.widgetservice.widget.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WidgetRepositoryTest {

    @InjectMocks
    private WidgetRepository repository;

    @Test
    public void shouldSaveWidget() {
        var widget = Widget.builder()
                .coordinates(new Point(0,0))
                .height(100)
                .width(100)
                .zIndex(2)
                .build();

        var saved = repository.save(widget);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCoordinates()).isEqualTo(widget.getCoordinates());
        assertThat(saved.getHeight()).isEqualTo(widget.getHeight());
        assertThat(saved.getWidth()).isEqualTo(widget.getWidth());
        assertThat(saved.getZIndex()).isEqualTo(widget.getZIndex());
        assertThat(saved.getModifiedAt()).isNotNull();
        assertThat(repository.fetchAll()).hasSize(1);
    }
}