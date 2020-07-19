package miro.widgetservice.ratelimit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionManagerTest {

    @Test
    public void allowRequestIfCounterIsLowerThanConfig() {
        var ip = "127.0.0.1";
        var handlerIdentity = "GET /widgets";

        var rateLimitConfig = mock(RateLimit.class);
        when(rateLimitConfig.allowedRequests()).thenReturn(1);

        var manager = new SessionManager(Map.of(ip, new Session()));
        assertThat(manager.allowRequest(handlerIdentity, rateLimitConfig, ip)).isTrue();
        assertThat(manager.allowRequest(handlerIdentity, rateLimitConfig, ip)).isFalse();
    }
}