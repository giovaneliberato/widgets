package miro.widgetservice.widget.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.*;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

}