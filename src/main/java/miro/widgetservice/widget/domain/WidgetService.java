package miro.widgetservice.widget.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WidgetService {

    @Autowired
    private WidgetRepository repository;

    public Widget create(Widget widget) {
        return repository.save(widget);
    }

}
