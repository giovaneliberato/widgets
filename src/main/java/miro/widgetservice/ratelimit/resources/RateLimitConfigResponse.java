package miro.widgetservice.ratelimit.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import miro.widgetservice.ratelimit.domain.RateLimitConfig;

import java.io.Serializable;

@Builder
public class RateLimitConfigResponse implements Serializable {

    @JsonProperty("handler_identity")
    private String handlerIdentity;

    @JsonProperty("allowed_requests")
    private Integer allowedRequests;

    @JsonProperty("time_window_in_seconds")
    private Integer timeWindowInSeconds;

    @JsonProperty("configured_by")
    private RateLimitConfig.ConfigType configuredBy;

    public static RateLimitConfigResponse convert(RateLimitConfig config) {
        return RateLimitConfigResponse.builder()
                .handlerIdentity(config.getHandlerIdentity())
                .allowedRequests(config.getAllowedRequests())
                .timeWindowInSeconds(config.getTimeWindowInSeconds())
                .configuredBy(config.getConfiguredBy())
                .build();
    }
}
