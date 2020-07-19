package miro.widgetservice.widget.domain;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static java.util.Comparator.comparingInt;

@Repository
public class WidgetRepository {

    private final List<Widget> store = new LinkedList<>();

    public Widget save(Widget widget) {
        widget = widget
                .withId(UUID.randomUUID())
                .withModifiedAt(Instant.now());

        store.add(widget);
        return widget;
    }

    public Widget getTopWidget() {
        return store.stream()
                .max(comparingInt(Widget::getZIndex))
                .get();
    }

    public List<Widget> fetchAll() {
        return store;
    }
}
