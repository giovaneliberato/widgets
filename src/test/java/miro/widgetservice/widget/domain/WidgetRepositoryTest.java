package miro.widgetservice.widget.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.*;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(repository.getAll()).hasSize(1);
    }

    @Test
    public void shouldGetTopWidget() {
        var widget1 = Widget.builder()
                .coordinates(new Point(0,0))
                .height(100)
                .width(100)
                .zIndex(4)
                .build();
        var widget2 = widget1.withZIndex(1);
        var widget3 = widget1.withZIndex(2);

        repository.save(widget1);
        repository.save(widget2);
        repository.save(widget3);

        var topWidget = repository.getTopWidget();

        assertThat(topWidget.getZIndex()).isEqualTo(4);
    }

    @Test
    public void shouldValidateIfZIndexAlreadyInUse() {
        var widget1 = Widget.builder()
                .coordinates(new Point(0,0))
                .height(100)
                .width(100)
                .zIndex(2)
                .build();

        repository.save(widget1);

        assertThat(repository.zIndexAlreadyInUse(2)).isTrue();
        assertThat(repository.zIndexAlreadyInUse(1)).isFalse();
    }

    @Test
    public void shouldGetOverlaidWidgets() {
        var widget1 = Widget.builder()
                .coordinates(new Point(0,0))
                .height(100)
                .width(100)
                .zIndex(2)
                .build();
        var widget2 = widget1.withZIndex(-1);
        var widget3 = widget1.withZIndex(3);

        repository.save(widget1);
        repository.save(widget2);
        repository.save(widget3);

        var overlaidWidgets = repository.getOverlaidWidgets(2);
        assertThat(overlaidWidgets).hasSize(2);
        assertThat(overlaidWidgets.get(0).getZIndex()).isEqualTo(3);
        assertThat(overlaidWidgets.get(1).getZIndex()).isEqualTo(2);
    }

    @Test
    public void shouldUpdateOnlyApplicableWidgets() {
        var widget1 = repository.save(Widget.builder()
                .zIndex(1)
                .build());

        var widget2 = repository.save(Widget.builder()
                .zIndex(2)
                .build());

        var widget3 = repository.save(Widget.builder()
                .zIndex(3)
                .build());

        repository.update(asList(widget1, widget2));

        var widgets = repository.getAll().stream().collect(toMap(Widget::getId, Function.identity()));
        assertThat(widgets.get(widget1.getId()).getModifiedAt()).isAfter(widget1.getModifiedAt());
        assertThat(widgets.get(widget2.getId()).getModifiedAt()).isAfter(widget2.getModifiedAt());
        assertThat(widgets.get(widget3.getId()).getModifiedAt()).isEqualTo(widget3.getModifiedAt());
    }

    @Test
    public void shouldGetWidgetById() {
        repository.save(Widget.builder()
                .coordinates(new Point(0,0))
                .height(100)
                .width(100)
                .zIndex(1)
                .build());

        var widget2 = repository.save(Widget.builder()
                .coordinates(new Point(0,0))
                .height(200)
                .width(200)
                .zIndex(2)
                .build());

        var retrieved = repository.getById(widget2.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getId()).isEqualTo(widget2.getId());
    }

    @Test
    public void shouldReturnEmptyWhenWidgetByIdNotFound() {
        var retrieved = repository.getById(UUID.randomUUID());
        assertThat(retrieved).isNotPresent();
    }

    @Test
    public void shouldDeleteWidgetById() throws WidgetNotFoundException {
        repository.save(Widget.builder()
                .coordinates(new Point(0,0))
                .height(100)
                .width(100)
                .zIndex(1)
                .build());

        var widget2 = repository.save(Widget.builder()
                .coordinates(new Point(0,0))
                .height(200)
                .width(200)
                .zIndex(2)
                .build());

        repository.deleteById(widget2.getId());
        assertThat(repository.getAll()).hasSize(1);
        assertThatThrownBy(() -> repository.deleteById(widget2.getId())).isInstanceOf(WidgetNotFoundException.class);
    }
}