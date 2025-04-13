package by.laba1.demo.core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
@RequiredArgsConstructor
public class VisitCounterInterceptor implements HandlerInterceptor {
    private final VisitCounterService visitCounterService;

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {

        String url = request.getRequestURI();
        // Исключаем статические ресурсы и эндпоинты статистики
        if (!url.startsWith("/static") && !url.startsWith("/api/logs/stats")) {
            visitCounterService.incrementVisitCount(url);
            log.info("Visit counted for URL: {}", url);
        }
    }
}
