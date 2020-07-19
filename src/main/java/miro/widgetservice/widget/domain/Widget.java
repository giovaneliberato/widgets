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
public class Widget implements Serializable {

    @With
    private UUID id;

    private Point coordinates;

    private Integer zIndex;

    private Integer width;

    private Integer height;

    @With
    private Instant modifiedAt;

}
