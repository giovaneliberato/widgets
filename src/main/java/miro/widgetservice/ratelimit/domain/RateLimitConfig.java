package miro.widgetservice.ratelimit.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

@Builder
@Getter
public class RateLimitConfig {

    private String handlerIdentity;

    private Integer allowedRequests;

    private Integer timeWindowInSeconds;

    @With
    private ConfigType configuredBy;

    public enum ConfigType {
        DEFAULT,
        DEFINED_BY_CLASS,
        DEFINED_BY_METHOD,
        OVERRIDDEN_BY_ADMIN
    }
}
