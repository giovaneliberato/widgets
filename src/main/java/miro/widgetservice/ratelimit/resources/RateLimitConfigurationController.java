package miro.widgetservice.ratelimit.resources;

import miro.widgetservice.ratelimit.domain.RateLimitConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static java.util.stream.Collectors.toList;

@RestController
public class RateLimitConfigurationController {

    @Autowired
    private RateLimitConfigManager manager;

    @GetMapping("/admin/rate-limit")
    public ResponseEntity getAllRateLimitConfigs() {
        return ResponseEntity.ok(
                manager.getAllConfigs()
                        .stream()
                        .map(RateLimitConfigResponse::convert)
                        .collect(toList())
        );
    }

    @PostMapping("/admin/rate-limit")
    public ResponseEntity createConfig(@Valid @RequestBody RateLimitUpdateRequest request) {
        manager.create(request.toDomain());

        return getAllRateLimitConfigs();
    }

}
