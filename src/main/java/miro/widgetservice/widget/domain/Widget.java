package miro.widgetservice.widget.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.awt.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@With
public class Widget implements Serializable {

    private UUID id;

    private Point coordinates;

    private Integer zIndex;

    private Integer width;

    private Integer height;

    private Instant modifiedAt;

}
