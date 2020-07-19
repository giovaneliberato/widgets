package miro.widgetservice.widget.resources;

import miro.widgetservice.widget.domain.WidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Controller
public class WidgetController {

    @Autowired
    private WidgetService service;


    @PostMapping("/widget")
    public ResponseEntity<WidgetResponse> createWidget(@Valid @RequestBody WidgetCreationRequest request) {

        var created = service.create(request.convertToDomain());

        return new ResponseEntity(
                WidgetResponse.convertFromDomain(created),
                HttpStatus.CREATED
        );
    }

}
