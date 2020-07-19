package miro.widgetservice.widget.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.*;

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

}