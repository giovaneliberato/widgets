package miro.widgetservice.ratelimit.resources;

import miro.widgetservice.ratelimit.domain.RateLimit;
import miro.widgetservice.ratelimit.domain.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Objects.isNull;

public class RateLimitHttpInterceptor implements HandlerInterceptor {

    @Autowired
    private SessionManager sessionManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var handlerMethod = (HandlerMethod) handler;
        var rateLimitConfiguration = handlerMethod.getMethodAnnotation(RateLimit.class);

        if (isNull(rateLimitConfiguration)) {
            return sessionManager.allowRequestForDefaultConfig(request.getRemoteAddr());
        }
        return sessionManager.allowRequest(rateLimitConfiguration.handlerIdentity(), request.getRemoteAddr());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }


}
