package miro.widgetservice.widget.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import miro.widgetservice.widget.domain.Widget;

import java.awt.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
public class WidgetResponse implements Serializable {

    @JsonProperty
    private UUID id;

    @JsonProperty
    private Point coordinates;

    @JsonProperty("z_index")
    private Integer zIndex;

    @JsonProperty
    private Integer width;

    @JsonProperty
    private Integer height;

    @JsonProperty("modified_at")
    private Instant modifiedAt;

    public static WidgetResponse convertFromDomain(Widget widget) {
        return WidgetResponse.builder()
                .id(widget.getId())
                .coordinates(widget.getCoordinates())
                .zIndex(widget.getZIndex())
                .width(widget.getWidth())
                .height(widget.getHeight())
                .modifiedAt(widget.getModifiedAt())
                .build();
    }

}
