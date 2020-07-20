package miro.widgetservice.ratelimit.domain;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    String handlerIdentity();
    int allowedRequests() default -1;
    int timeWindowInSeconds() default -1;

}
