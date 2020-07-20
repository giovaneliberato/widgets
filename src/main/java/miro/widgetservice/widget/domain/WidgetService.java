package miro.widgetservice.widget.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static miro.widgetservice.ObjectUtils.coalesce;

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

    public Optional<String> deleteById(UUID widgetId) {
        try {
            repository.deleteById(widgetId);
            return Optional.empty();
        } catch (WidgetNotFoundException e) {
            return Optional.of(e.getMessage());
        }
    }

    public Optional<Widget> getById(UUID widgetId) {
        return repository.getById(widgetId);
    }

    public Widget updateWidget(UUID widgetId, Widget updatedData) throws WidgetNotFoundException {
        var widget = repository.getById(widgetId)
                .map(current -> patchWidget(current, updatedData))
                .orElseThrow(WidgetNotFoundException::new);

        return repository.update(widget);
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

    private Widget patchWidget(Widget current, Widget toUpdate) {
        return current
                .withZIndex(coalesce(toUpdate.getZIndex(), current.getZIndex()))
                .withCoordinates(coalesce(toUpdate.getCoordinates(), current.getCoordinates()))
                .withWidth(coalesce(toUpdate.getWidth(), current.getWidth()))
                .withHeight(coalesce(toUpdate.getHeight(), current.getHeight()));
    }

    public List<Widget> getAllWidgets() {
        return repository.getAll();
    }
}
