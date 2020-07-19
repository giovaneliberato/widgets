package miro.widgetservice.widget;

import lombok.Getter;

import java.awt.*;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Widget {

    private UUID id;

    private Point coordinate;

    private Integer zIndex;

    private Integer width;

    private Integer height;

    private Instant modifiedAt;

}
