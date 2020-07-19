package miro.widgetservice.widget.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import miro.widgetservice.widget.domain.Widget;

import java.awt.*;
import java.io.Serializable;

@Builder
@Getter
@AllArgsConstructor
public class WidgetUpdateRequest implements Serializable {

    private Point coordinates;

    @JsonProperty("z_index")
    private Integer zIndex;

    private Integer width;

    private Integer height;

    public Widget convertToDomain() {
        return Widget.builder()
                .coordinates(coordinates)
                .height(height)
                .width(width)
                .zIndex(zIndex)
                .build();
    }
}
