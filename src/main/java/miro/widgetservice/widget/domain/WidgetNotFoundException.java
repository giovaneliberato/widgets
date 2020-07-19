package miro.widgetservice.widget.domain;

public class WidgetNotFoundException extends Exception {
    public WidgetNotFoundException() {
        super("Widget with ID not found");
    }
}
