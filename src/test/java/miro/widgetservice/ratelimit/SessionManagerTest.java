package miro.widgetservice.ratelimit;

import miro.widgetservice.ratelimit.domain.RateLimit;
import miro.widgetservice.ratelimit.domain.RateLimitConfig;
import miro.widgetservice.ratelimit.domain.RateLimitConfigManager;
import miro.widgetservice.ratelimit.domain.SessionManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionManagerTest {

    @InjectMocks
    private SessionManager manager;

    @Mock
    private RateLimitConfigManager rateLimitConfigManager;

    @Test
    public void allowRequestOnlyIfCounterIsLowerThanConfig() {
        var ip = "127.0.0.1";
        var handlerIdentity = "getAllWidgets";

        when(rateLimitConfigManager.getByHandlerIdentifier(handlerIdentity)).thenReturn(
                RateLimitConfig.builder()
                        .allowedRequests(1)
                        .timeWindowInSeconds(60)
                        .build());

        assertThat(manager.allowRequest(handlerIdentity, ip)).isTrue();
        assertThat(manager.allowRequest(handlerIdentity, ip)).isFalse();
    }

    @Test
    public void requestManagerShouldWorkForMultipleHandlers() {
        var ip = "127.0.0.1";
        var handlerIdentity1 = "getAllWidgets";
        var handlerIdentity2 = "patchWidget";

        when(rateLimitConfigManager.getByHandlerIdentifier(handlerIdentity1)).thenReturn(
                RateLimitConfig.builder()
                        .allowedRequests(1)
                        .timeWindowInSeconds(60)
                        .build());

        when(rateLimitConfigManager.getByHandlerIdentifier(handlerIdentity2)).thenReturn(
                RateLimitConfig.builder()
                        .allowedRequests(2)
                        .timeWindowInSeconds(60)
                        .build());

        assertThat(manager.allowRequest(handlerIdentity1, ip)).isTrue();
        assertThat(manager.allowRequest(handlerIdentity2, ip)).isTrue();
        assertThat(manager.allowRequest(handlerIdentity1, ip)).isFalse();
        assertThat(manager.allowRequest(handlerIdentity2, ip)).isTrue();
        assertThat(manager.allowRequest(handlerIdentity2, ip)).isFalse();
    }

    @Test
    public void shouldResetCounterAfterTimeWindowExpires() throws InterruptedException {
        var ip = "127.0.0.1";
        var handlerIdentity = "getAllWidgets";

        when(rateLimitConfigManager.getByHandlerIdentifier(handlerIdentity)).thenReturn(
                RateLimitConfig.builder()
                        .allowedRequests(1)
                        .timeWindowInSeconds(1)
                        .build());

        assertThat(manager.allowRequest(handlerIdentity, ip)).isTrue();
        assertThat(manager.allowRequest(handlerIdentity, ip)).isFalse();

        Thread.sleep(1100);
        assertThat(manager.allowRequest(handlerIdentity, ip)).isTrue();
    }
}