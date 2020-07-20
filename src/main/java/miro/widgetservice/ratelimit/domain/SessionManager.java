package miro.widgetservice.ratelimit.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class SessionManager {

    @Autowired
    private RateLimitConfigManager rateLimitConfigManager;

    private final Map<String, Session> sessions = new HashMap<>();

    public boolean allowRequestForDefaultConfig(String ip) {
        return allowRequest("*", ip);
    }

    public boolean allowRequest(String handlerIdentity, String ip) {
        var config = rateLimitConfigManager.getByHandlerIdentifier(handlerIdentity);
        var session = getOrCreateSession(handlerIdentity, config, ip);
        var sessionWindow = session.getOrCreateSessionWindow(handlerIdentity, config.getTimeWindowInSeconds());

        sessionWindow.resetIfNeeded();

        if (sessionWindow.getTotalRequests() < config.getAllowedRequests()) {
            session.incrementRequestCounter(handlerIdentity);
            return true;
        }

        return false;
    }

    private Session getOrCreateSession(String handlerIdentity, RateLimitConfig rateLimitConfiguration, String ip) {
        if (sessions.containsKey(ip)) {
            return sessions.get(ip);
        }
        var session = Session.create(handlerIdentity, rateLimitConfiguration.getTimeWindowInSeconds());
        sessions.put(ip, session);
        return session;
    }
}
