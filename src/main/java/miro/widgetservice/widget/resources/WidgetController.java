package miro.widgetservice.widget.resources;

import miro.widgetservice.widget.domain.WidgetNotFoundException;
import miro.widgetservice.widget.domain.WidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.*;

@Controller
public class WidgetController {

    @Autowired
    private WidgetService service;


    @PostMapping("/widgets")
    public ResponseEntity<WidgetResponse> createWidget(@Valid @RequestBody WidgetCreationRequest request) {
        var created = service.create(request.convertToDomain());

        return new ResponseEntity(
                WidgetResponse.convertFromDomain(created),
                CREATED
        );
    }

    @GetMapping("/widgets/{widgetId}")
    public ResponseEntity getWidget(@PathVariable("widgetId") UUID widgetId) {
        return service.getById(widgetId)
                .map(widget -> new ResponseEntity(WidgetResponse.convertFromDomain(widget), OK))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/widgets")
    public ResponseEntity getAllWidgets() {
        return ResponseEntity.ok(service.getAllWidgets()
                .stream()
                .map(WidgetResponse::convertFromDomain)
                .collect(toList()));
    }

    @DeleteMapping("/widgets/{widgetId}")
    public ResponseEntity deleteWidget(@PathVariable("widgetId") UUID widgetId) {
        return service.deleteById(widgetId)
                .map(error -> new ResponseEntity(error, BAD_REQUEST))
                .orElse(ResponseEntity.ok().build());
    }

    @PatchMapping("/widgets/{widgetId}")
    public ResponseEntity<WidgetResponse> updateWidget(
            @PathVariable("widgetId") UUID widgetId,
            @RequestBody WidgetUpdateRequest request) {

        try {
            var updated = service.updateWidget(widgetId, request.convertToDomain());
            return ResponseEntity.ok(WidgetResponse.convertFromDomain(updated));
        } catch (WidgetNotFoundException e) {
            return new ResponseEntity(e.getMessage(), BAD_REQUEST);
        }
    }
}
