package by.laba1.demo.core.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final VisitCounterInterceptor visitCounterInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(visitCounterInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/static/**",
                "/webjars/**",
                "/favicon.ico",
                "/api/logs/stats/**"
            );
    }
}