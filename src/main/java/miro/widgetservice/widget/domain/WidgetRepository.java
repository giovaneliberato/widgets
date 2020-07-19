package miro.widgetservice.widget.domain;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Repository
public class WidgetRepository {

    private List<Widget> store = new LinkedList<>();

    public Widget save(Widget widget) {
        widget = widget
                .withId(UUID.randomUUID())
                .withModifiedAt(Instant.now());

        store.add(widget);
        return widget;
    }

    public Widget getTopWidget() {
        return getAll().get(0);
    }

    public List<Widget> getAll() {
        return store.stream()
                .sorted(comparingInt(Widget::getZIndex).reversed())
                .collect(toList());
    }

    public boolean zIndexAlreadyInUse(Integer zIndex) {
        return getAll().stream()
                .anyMatch(w -> w.getZIndex().equals(zIndex));
    }

    public List<Widget> getOverlaidWidgets(Integer pointer) {
        return getAll().stream()
                .filter(w -> w.getZIndex() >= pointer)
                .collect(toList());
    }

    public void update(List<Widget> toUpdate) {
        var idsToUpdate = toUpdate.stream().collect(toMap(Widget::getId, Function.identity()));

        store = getAll().stream()
                .map(w -> updateWidgetIById(idsToUpdate, w))
                .collect(toList());
    }

    private Widget updateWidgetIById(Map<UUID, Widget> widgetsToUpdate, Widget current) {
        if (widgetsToUpdate.containsKey(current.getId())) {
            return widgetsToUpdate.get(current.getId()).withModifiedAt(Instant.now());
        }
        return current;
    }

    public Optional<Widget> getById(UUID widgetId) {
        return getAll()
                .stream()
                .filter(w -> w.getId().equals(widgetId))
                .findFirst();
    }
}
