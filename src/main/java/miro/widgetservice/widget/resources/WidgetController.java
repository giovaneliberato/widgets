package miro.widgetservice.widget.resources;

import miro.widgetservice.widget.domain.WidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class WidgetController {

    @Autowired
    private WidgetService service;


    @PostMapping("/widget")
    public ResponseEntity<WidgetResponse> createWidget(@Valid @RequestBody WidgetCreationRequest request) {

        var created = service.create(request.convertToDomain());

        return new ResponseEntity(
                WidgetResponse.convertFromDomain(created),
                CREATED
        );
    }

    @GetMapping("/widget/{widgetId}")
    public ResponseEntity createWidget(@PathVariable("widgetId") UUID widgetId) {
        return service.getById(widgetId)
                .map(widget -> new ResponseEntity(WidgetResponse.convertFromDomain(widget), OK))
                .orElse(ResponseEntity.notFound().build());
    }
}
