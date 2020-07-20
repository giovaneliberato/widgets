package miro.widgetservice.config;

import miro.widgetservice.ratelimit.resources.RateLimitHttpInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    @Bean
    public RateLimitHttpInterceptor rateLimitHttpInterceptor() {
        return new RateLimitHttpInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitHttpInterceptor());
    }
}
