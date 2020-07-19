package miro.widgetservice.widget.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Component
public class WidgetService {

    @Autowired
    private WidgetRepository repository;

    public Widget create(Widget widget) {
        if (isNull(widget.getZIndex())) {
            widget = widget.withZIndex(getForegroundZIndex());
        } else if (repository.zIndexAlreadyInUse(widget.getZIndex())) {
            shiftOverlaidWidgetsForward(widget);
        }

        return repository.save(widget);
    }

    private Integer getForegroundZIndex() {
        return repository.getTopWidget().getZIndex() + 1;
    }

    private void shiftOverlaidWidgetsForward(Widget widget) {
        var updatedWidgets = repository.getOverlaidWidgets(widget.getZIndex()).stream()
                .map(this::incrementZIndex)
                .collect(toList());

        repository.update(updatedWidgets);
    }

    private Widget incrementZIndex(Widget widget) {
        return widget.withZIndex(widget.getZIndex() + 1);
    }

}
