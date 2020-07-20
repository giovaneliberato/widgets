package miro.widgetservice.ratelimit.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import miro.widgetservice.ratelimit.domain.RateLimitConfig;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Builder
public class RateLimitUpdateRequest implements Serializable {
    @NotEmpty
    @JsonProperty("handler_identity")
    private String handlerIdentity;

    @NotEmpty
    @JsonProperty("allowed_requests")
    private Integer allowedRequests;

    @NotEmpty
    @JsonProperty("time_window_in_seconds")
    private Integer timeWindowInSeconds;

    public RateLimitConfig toDomain() {
        return RateLimitConfig.builder()
                .handlerIdentity(handlerIdentity)
                .allowedRequests(allowedRequests)
                .timeWindowInSeconds(timeWindowInSeconds)
                .build();
    }

}
