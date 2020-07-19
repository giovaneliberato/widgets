package miro.widgetservice.ratelimit;


import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Session {

    private Map<String, SessionWindow> requestCounter = new HashMap<>();

    private Session(String handlerIdentifier, Integer durationInSeconds) {
        requestCounter.put(handlerIdentifier, SessionWindow.ofSeconds(durationInSeconds));
    }

    public static Session create(String handlerIdentity, Integer durationInSeconds) {
        return new Session(handlerIdentity, durationInSeconds);
    }

    public SessionWindow getOrCreateSessionWindow(String handlerIdentity, Integer durationInSeconds) {
        requestCounter.putIfAbsent(handlerIdentity, SessionWindow.ofSeconds(durationInSeconds));
        return requestCounter.get(handlerIdentity);
    }

    public void incrementRequestCounter(String handlerIdentity) {
        var requestCount = requestCounter.get(handlerIdentity);
        requestCounter.put(handlerIdentity, requestCount.incrementRequests());
    }

    @Getter
    @Builder
    public static class SessionWindow {

        public static SessionWindow ofSeconds(Integer durationInSeconds) {
            return SessionWindow.builder()
                    .durationInSeconds(durationInSeconds)
                    .totalRequests(0)
                    .resetsAt(Instant.now().plusSeconds((long) durationInSeconds))
                    .build();
        }

        private Integer totalRequests;

        private Integer durationInSeconds;

        private Instant resetsAt;

        public SessionWindow incrementRequests() {
            totalRequests += 1;
            return this;
        }

        public void resetIfNeeded() {
           if (Instant.now().isAfter(resetsAt)) {
               totalRequests = 0;
               resetsAt = Instant.now().plusSeconds(durationInSeconds);
           }
        }
    }
}
