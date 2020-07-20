package miro.widgetservice.ratelimit.domain;

import miro.widgetservice.widget.resources.WidgetController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static miro.widgetservice.ObjectUtils.coalesce;

@Component
public class RateLimitConfigManager {

    @Value("${rate-limit.requests:1000}")
    private Integer defaultRequestLimit;

    @Value("${rate-limit.time-window-in-seconds:60}")
    private Integer defaultTimeWindow;

    @Autowired
    private ApplicationContext applicationContext;

    private List<RateLimitConfig> rateLimitConfigList;

    private RateLimitConfig DEFAULT_CONFIG;

    @PostConstruct
    public void init() {
        rateLimitConfigList = new LinkedList<>();
        addCustomDefinedConfigs();
    }

    public RateLimitConfig getByHandlerIdentifier(String handlerIdentifier) {
        return getAllConfigs()
                .stream()
                .filter(c -> c.getHandlerIdentity().equals(handlerIdentifier))
                .findFirst()
                .orElse(DEFAULT_CONFIG);
    }


    public List<RateLimitConfig> getAllConfigs() {
        return rateLimitConfigList
                .stream()
                .sorted(comparing(RateLimitConfig::getConfiguredBy).reversed())
                .collect(toList());
    }

    private void addCustomDefinedConfigs() {
        DEFAULT_CONFIG = RateLimitConfig.builder()
                .handlerIdentity("*")
                .allowedRequests(defaultRequestLimit)
                .timeWindowInSeconds(defaultTimeWindow)
                .configuredBy(RateLimitConfig.ConfigType.DEFAULT)
                .build();

        rateLimitConfigList.add(DEFAULT_CONFIG);


        var widgetController = applicationContext.getBean(WidgetController.class);
        var baseConfig = widgetController.getClass().getAnnotation(RateLimit.class);

        if (!isNull(baseConfig)) {
            var controllerConfig = RateLimitConfig.builder()
                    .allowedRequests(providedOrDefault(baseConfig.allowedRequests(), defaultRequestLimit))
                    .timeWindowInSeconds(providedOrDefault(baseConfig.timeWindowInSeconds(), defaultTimeWindow))
                    .handlerIdentity(baseConfig.handlerIdentity())
                    .configuredBy(RateLimitConfig.ConfigType.DEFINED_BY_CLASS)
                    .build();
            rateLimitConfigList.add(controllerConfig);
        }

        Stream.of(widgetController.getClass().getMethods())
                .filter(m -> m.isAnnotationPresent(RateLimit.class))
                .map(m -> m.getAnnotation(RateLimit.class))
                .map(rateLimit -> RateLimitConfig.builder()
                        .allowedRequests(providedOrDefault(rateLimit.allowedRequests(), defaultRequestLimit))
                        .timeWindowInSeconds(providedOrDefault(rateLimit.timeWindowInSeconds(), defaultTimeWindow))
                        .handlerIdentity(rateLimit.handlerIdentity())
                        .configuredBy(RateLimitConfig.ConfigType.DEFINED_BY_METHOD)
                        .build())
                .forEach(rateLimitConfigList::add);
    }

    private Integer providedOrDefault(int i, Integer defaultValue) {
        return i == -1 ? defaultValue : i;
    }
}
