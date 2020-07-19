package miro.widgetservice.widget.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class WidgetService {

    @Autowired
    private WidgetRepository repository;

    public Widget create(Widget widget) {
        if (isNull(widget.getZIndex())) {
            widget = widget.withZIndex(getForegroundZIndex());
        }

        return repository.save(widget);
    }

    private Integer getForegroundZIndex() {
        return repository.getTopWidget().getZIndex() + 1;
    }

}
