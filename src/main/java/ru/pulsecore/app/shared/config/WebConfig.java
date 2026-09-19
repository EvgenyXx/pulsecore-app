package ru.pulsecore.app.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.pulsecore.app.player.infrastructure.interceptor.SubscriptionInterceptor;
import ru.pulsecore.app.shared.security.CurrentPlayerArgumentResolver;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final SubscriptionInterceptor subscriptionInterceptor;
    private final CurrentPlayerArgumentResolver currentPlayerArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(subscriptionInterceptor)
                .addPathPatterns(
                        // Главная — открыта
//              "/api/tournament/*/dashboard",

                        // Расписание — открыто
//              "/api/player/halls",

                        // Сумма — защищена
                        "/api/player/*/sum",
                        "/api/tournament/sum**",

                        // Турниры — защищены
                        "/api/player/*/tournaments",

                        // Зал славы — открыт
//              "/api/tournament/top/**",

                        // Аналитика — ЗАЩИЩЕНА
//                        "/api/tournament/analytics",
//                        "/api/tournament/best-time",
//                        "/api/tournament/monthly-income",
//                        "/api/tournament/daily-income",

                        // Live — защищён
                        "/api/tournament/live",
                        "/api/player/live/halls",
                        "/api/player/live-halls",

                        // 2×2 — защищён
                        "/api/tournament/compare/**"
                );
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/dashboard").setViewName("forward:/dashboard.html");
        registry.addViewController("/profile").setViewName("forward:/profile.html");
        registry.addViewController("/admin").setViewName("forward:/admin.html");
        registry.addViewController("/register").setViewName("forward:/register.html");
        registry.addViewController("/subscribe").setViewName("forward:/subscribe.html");
        registry.addViewController("/analytics").setViewName("forward:/analytics.html");
        registry.addViewController("/live").setViewName("forward:/live.html");
        registry.addViewController("/oauth-finish").setViewName("forward:/oauth-finish.html");
        registry.addViewController("/live/**").setViewName("forward:/live-tournament.html");
        registry.addViewController("/compare").setViewName("forward:/compare.html");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentPlayerArgumentResolver);
    }
}