package miro.widgetservice.widget.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WidgetServiceTest {

    @InjectMocks
    private WidgetService service;

    @Mock
    private WidgetRepository repository;

    @Test
    public void shouldSaveWidgetToRepository() {
        var widget = Widget.builder()
                .coordinates(new Point(0,0))
                .height(100)
                .width(100)
                .zIndex(2)
                .build();

        when(repository.save(widget)).then(returnsFirstArg());

        var saved = service.create(widget);

        assertThat(saved).isNotNull();
        verify(repository).save(any(Widget.class));
    }

    @Test
    public void shouldDefineZIndexIfNotProvided() {
        var preExistingWidget1 = Widget.builder()
                .zIndex(1)
                .build();

        var newWidget = Widget.builder()
                .coordinates(new Point(0,0))
                .height(100)
                .width(100)
                .build();

        when(repository.getTopWidget())
                .thenReturn(preExistingWidget1);

        when(repository.save(any())).then(returnsFirstArg());

        var saved = service.create(newWidget);

        assertThat(saved.getZIndex()).isEqualTo(2);
    }

    @Test
    public void shouldShiftAllZIndexUpOnConflict() {
        var widget = Widget.builder()
                .coordinates(new Point(0,0))
                .height(100)
                .width(100)
                .zIndex(1)
                .build();

        var captor = ArgumentCaptor.forClass(List.class);

        when(repository.zIndexAlreadyInUse(1)).thenReturn(true);
        when(repository.getOverlaidWidgets(1)).thenReturn(asList(
                Widget.builder().zIndex(1).build(),
                Widget.builder().zIndex(2).build(),
                Widget.builder().zIndex(3).build()
        ));
        when(repository.save(any())).then(returnsFirstArg());

        service.create(widget);

        verify(repository).update(captor.capture());
        var updated = captor.getValue();

        assertThat(((Widget) updated.get(0)).getZIndex()).isEqualTo(2);
        assertThat(((Widget) updated.get(1)).getZIndex()).isEqualTo(3);
        assertThat(((Widget) updated.get(2)).getZIndex()).isEqualTo(4);
    }

    @Test
    public void shouldReturnErrorWhenWidgetToBeDeletesIsNotFound() throws WidgetNotFoundException {
        doThrow(new WidgetNotFoundException()).when(repository).deleteById(any());
        assertThat(service.deleteById(UUID.randomUUID())).isPresent();
    }

    @Test
    public void shouldNotReturnErrorWhenWidgetToBeDeletesIsFound() throws WidgetNotFoundException {
        doNothing().when(repository).deleteById(any());
        assertThat(service.deleteById(UUID.randomUUID())).isNotPresent();
    }

    @Test
    public void shouldUpdateAllAttributes() throws WidgetNotFoundException {
        var current = Widget.builder()
                .id(UUID.randomUUID())
                .coordinates(new Point(0,0))
                .height(100)
                .width(100)
                .zIndex(1)
                .build();

        var updatedData = current
                .withCoordinates(new Point(100, 100))
                .withWidth(200)
                .withHeight(200)
                .withZIndex(-1);

        when(repository.getById(current.getId())).thenReturn(Optional.of(current));
        when(repository.update(any(Widget.class))).then(returnsFirstArg());

        var updated = service.updateWidget(current.getId(), updatedData);

        assertThat(updated.getId()).isEqualTo(current.getId());
        assertThat(updated.getCoordinates()).isEqualTo(updatedData.getCoordinates());
        assertThat(updated.getHeight()).isEqualTo(updatedData.getHeight());
        assertThat(updated.getWidth()).isEqualTo(updatedData.getWidth());
        assertThat(updated.getZIndex()).isEqualTo(updatedData.getZIndex());
    }

    @Test
    public void shouldNotOverrideAttributesIfValusIsNull() throws WidgetNotFoundException {
        var current = Widget.builder()
                .id(UUID.randomUUID())
                .coordinates(new Point(0,0))
                .height(100)
                .width(100)
                .zIndex(1)
                .build();

        var updatedData = current
                .withCoordinates(new Point(100, 100))
                .withWidth(null)
                .withHeight(null);

        when(repository.getById(current.getId())).thenReturn(Optional.of(current));
        when(repository.update(any(Widget.class))).then(returnsFirstArg());

        var updated = service.updateWidget(current.getId(), updatedData);

        assertThat(updated.getId()).isEqualTo(current.getId());
        assertThat(updated.getCoordinates()).isEqualTo(updatedData.getCoordinates());
        assertThat(updated.getHeight()).isEqualTo(current.getHeight());
        assertThat(updated.getWidth()).isEqualTo(current.getWidth());
        assertThat(updated.getZIndex()).isEqualTo(current.getZIndex());
    }

    @Test
    public void shouldNotUpdateId() throws WidgetNotFoundException {
        var current = Widget.builder()
                .id(UUID.randomUUID())
                .coordinates(new Point(0,0))
                .height(100)
                .width(100)
                .zIndex(1)
                .build();

        var updatedData = current
                .withId(UUID.randomUUID());


        when(repository.getById(current.getId())).thenReturn(Optional.of(current));
        when(repository.update(any(Widget.class))).then(returnsFirstArg());

        var updated = service.updateWidget(current.getId(), updatedData);

        assertThat(updated.getId()).isEqualTo(current.getId());
    }
}