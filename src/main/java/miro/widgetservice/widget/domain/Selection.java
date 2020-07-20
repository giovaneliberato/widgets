package miro.widgetservice.widget.domain;

import lombok.Builder;
import lombok.Getter;

import java.awt.*;

@Builder
@Getter
public class Selection {

    private Point start;
    private Point end;

    public Integer getArea() {
        var xAxis = Math.pow(end.getX() + start.getX(), 2);
        var yAxis = Math.pow(end.getY() + start.getY(), 2);
        return (int) Math.sqrt(xAxis + yAxis);
    }
}
