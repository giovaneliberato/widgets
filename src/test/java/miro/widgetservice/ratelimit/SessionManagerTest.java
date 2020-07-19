package miro.widgetservice.ratelimit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionManagerTest {

    @Test
    public void allowRequestOnlyIfCounterIsLowerThanConfig() {
        var ip = "127.0.0.1";
        var handlerIdentity = "GET /widgets";

        var rateLimitConfig = mock(RateLimit.class);
        when(rateLimitConfig.allowedRequests()).thenReturn(1);
        when(rateLimitConfig.timeWindowInSeconds()).thenReturn(60);

        var manager = new SessionManager();
        assertThat(manager.allowRequest(handlerIdentity, rateLimitConfig, ip)).isTrue();
        assertThat(manager.allowRequest(handlerIdentity, rateLimitConfig, ip)).isFalse();
    }

    @Test
    public void requestManagerShouldWorkForMultipleHandlers() {
        var ip = "127.0.0.1";
        var handlerIdentity1 = "GET /widgets";
        var handlerIdentity2 = "PATCH /widgets/{widgetId}";

        var rateLimitConfig1 = mock(RateLimit.class);
        when(rateLimitConfig1.allowedRequests()).thenReturn(1);
        when(rateLimitConfig1.timeWindowInSeconds()).thenReturn(60);

        var rateLimitConfig2 = mock(RateLimit.class);
        when(rateLimitConfig2.allowedRequests()).thenReturn(2);
        when(rateLimitConfig2.timeWindowInSeconds()).thenReturn(60);

        var manager = new SessionManager();
        assertThat(manager.allowRequest(handlerIdentity1, rateLimitConfig1, ip)).isTrue();
        assertThat(manager.allowRequest(handlerIdentity2, rateLimitConfig2, ip)).isTrue();
        assertThat(manager.allowRequest(handlerIdentity1, rateLimitConfig1, ip)).isFalse();
        assertThat(manager.allowRequest(handlerIdentity2, rateLimitConfig2, ip)).isTrue();
        assertThat(manager.allowRequest(handlerIdentity2, rateLimitConfig2, ip)).isFalse();
    }

    @Test
    public void shouldResetCounterAfterTimeWindowExpires() throws InterruptedException {
        var ip = "127.0.0.1";
        var handlerIdentity = "GET /widgets";

        var rateLimitConfig = mock(RateLimit.class);
        when(rateLimitConfig.allowedRequests()).thenReturn(1);
        when(rateLimitConfig.timeWindowInSeconds()).thenReturn(1);

        var manager = new SessionManager();
        assertThat(manager.allowRequest(handlerIdentity, rateLimitConfig, ip)).isTrue();
        assertThat(manager.allowRequest(handlerIdentity, rateLimitConfig, ip)).isFalse();

        Thread.sleep(1100);
        assertThat(manager.allowRequest(handlerIdentity, rateLimitConfig, ip)).isTrue();
    }
}