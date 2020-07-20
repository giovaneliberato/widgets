package miro.widgetservice.ratelimit.resources;

import miro.widgetservice.ratelimit.domain.RateLimit;
import miro.widgetservice.ratelimit.domain.RateLimitConfig;
import miro.widgetservice.ratelimit.domain.RateLimitConfigManager;
import miro.widgetservice.ratelimit.domain.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Objects.isNull;

public class RateLimitHttpInterceptor implements HandlerInterceptor {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private RateLimitConfigManager rateLimitConfigManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var handlerMethod = (HandlerMethod) handler;
        var rateLimitConfiguration = getRateLimitConfiguration(handlerMethod);
        var handlerIdentifier = getHandlerIdentifier(rateLimitConfiguration, handlerMethod);

        if (sessionManager.allowRequest(handlerIdentifier, request.getRemoteAddr())) {
            return true;
        }

        throw new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        var handlerMethod = (HandlerMethod) handler;
        var rateLimitConfiguration = getRateLimitConfiguration(handlerMethod);
        var handlerIdentifier = getHandlerIdentifier(rateLimitConfiguration, handlerMethod);

        var rateLimitConfig = rateLimitConfigManager.getByHandlerIdentifier(handlerIdentifier);
        var sessionWindow = sessionManager.getSessionWindow(handlerIdentifier, request.getRemoteAddr());

        response.setHeader("X-Session-Available-Requests",
                String.valueOf(rateLimitConfig.getAllowedRequests() - sessionWindow.getTotalRequests()));
        response.setHeader("X-Session-Allowed-Requests", rateLimitConfig.getAllowedRequests().toString());
        response.setHeader("X-Session-Refresh-Datetime", sessionWindow.getResetsAt().toString());
    }


    private RateLimit getRateLimitConfiguration(HandlerMethod handler) {
        return handler.getMethodAnnotation(RateLimit.class);
    }

    private String getHandlerIdentifier(RateLimit rateLimitConfiguration, HandlerMethod handlerMethod) {
        if (isNull(rateLimitConfiguration)) {
            return handlerMethod.getMethod().getName();
        } else {
            return rateLimitConfiguration.handlerIdentity();
        }
    }



}
