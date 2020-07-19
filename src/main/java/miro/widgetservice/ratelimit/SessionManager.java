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
        var ipSession = sessions.getOrDefault(ip, new Session());

        if (ipSession.getRequestCounter(handlerIdentity) < rateLimitConfiguration.allowedRequests()) {
            ipSession.incrementRequestCounter(handlerIdentity);
            return true;
        }

        return false;
    }



}
