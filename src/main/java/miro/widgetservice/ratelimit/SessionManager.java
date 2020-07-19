package miro.widgetservice.ratelimit;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class SessionManager {

    private Map<String, Session> sessions = new HashMap<>();

    public boolean allowRequest(String handlerIdentity, RateLimit rateLimitConfiguration, String ip) {
        var session = getOrCreateSession(handlerIdentity, rateLimitConfiguration, ip);
        var sessionWindow = session.getOrCreateSessionWindow(handlerIdentity, rateLimitConfiguration.timeWindowInSeconds());

        sessionWindow.resetIfNeeded();

        if (sessionWindow.getTotalRequests() < rateLimitConfiguration.allowedRequests()) {
            session.incrementRequestCounter(handlerIdentity);
            return true;
        }

        return false;
    }

    private Session getOrCreateSession(String handlerIdentity, RateLimit rateLimitConfiguration, String ip) {
        if (sessions.containsKey(ip)) {
            return sessions.get(ip);
        }
        var session = Session.create(handlerIdentity, rateLimitConfiguration.timeWindowInSeconds());
        sessions.put(ip, session);
        return session;
    }

}
