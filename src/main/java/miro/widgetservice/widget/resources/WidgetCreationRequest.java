package miro.widgetservice.widget.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import miro.widgetservice.widget.domain.Widget;

import javax.validation.constraints.NotEmpty;
import java.awt.*;
import java.io.Serializable;

@Builder
@Getter
@AllArgsConstructor
public class WidgetCreationRequest implements Serializable {

    @NotEmpty
    private Point coordinates;

    @JsonProperty("z_index")
    private Integer zIndex;

    @NotEmpty
    private Integer width;

    @NotEmpty
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
