package miro.widgetservice.ratelimit;


import java.util.HashMap;
import java.util.Map;

public class Session {

    private Map<String, Integer> requestCounter = new HashMap<>();

    public Integer getRequestCounter(String handlerIdentity) {
        return requestCounter.getOrDefault(handlerIdentity, 0);
    }

    public void incrementRequestCounter(String handlerIdentity) {
        requestCounter.put(handlerIdentity, requestCounter.getOrDefault(handlerIdentity, 0) + 1);
    }
}
