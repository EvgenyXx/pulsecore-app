package ru.pulsecore.app.modules.shared.config;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import ru.pulsecore.app.modules.player.interceptor.SubscriptionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.pulsecore.app.modules.shared.security.CurrentPlayerArgumentResolver;

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
                        "/api/player/*/sum",
                        "/api/player/*/tournaments",
                        "/api/player/*/dashboard",
                        "/api/player/*/top/week",
                        "/api/player/*/top/week/*",
                        "/api/player/chat",
                        "/api/player/analytics",
                        "/api/player/*/monthly-income",
                        "/api/player/*/daily-income"
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
        registry.addViewController("/assistant").setViewName("forward:/assistant.html");
    }



    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentPlayerArgumentResolver);
    }
}