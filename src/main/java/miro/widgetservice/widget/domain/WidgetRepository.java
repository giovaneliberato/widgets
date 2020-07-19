package miro.widgetservice.widget.domain;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;

import static java.util.Collections.singletonList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

@Repository
public class WidgetRepository {

    private List<Widget> store = new LinkedList<>();

    public Widget save(Widget widget) {
        synchronized (store) {
            widget = widget
                .withId(UUID.randomUUID())
                .withModifiedAt(Instant.now());
            store.add(widget);
            return widget;
        }
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

    public Widget update(Widget toUpdate) {
        update(singletonList(toUpdate));
        return getById(toUpdate.getId())
                .orElseThrow(IllegalStateException::new);
    }

    public void update(List<Widget> toUpdate) {
        var idsToUpdate = toUpdate.stream().collect(toMap(Widget::getId, Function.identity()));
        synchronized (store) {
            store = getAll().stream()
                    .map(w -> updateWidgetIById(idsToUpdate, w))
                    .collect(toList());
        }
    }

    public Optional<Widget> getById(UUID widgetId) {
        return getAll()
                .stream()
                .filter(w -> w.getId().equals(widgetId))
                .findFirst();
    }

    public void deleteById(UUID widgetId) throws WidgetNotFoundException {
        synchronized (store) {
            var widgets = getAll().stream()
                    .collect(partitioningBy(w -> w.getId().equals(widgetId)));

            var found = !widgets.get(true).isEmpty();
            if (!found) {
                throw new WidgetNotFoundException();
            }

            store = widgets.get(false);
        }
    }

    public void dropAllWidgets() {
        synchronized (store) {
            store = new LinkedList<>();
        }
    }

    private Widget updateWidgetIById(Map<UUID, Widget> widgetsToUpdate, Widget current) {
        if (widgetsToUpdate.containsKey(current.getId())) {
            return widgetsToUpdate.get(current.getId()).withModifiedAt(Instant.now());
        }
        return current;
    }
}
