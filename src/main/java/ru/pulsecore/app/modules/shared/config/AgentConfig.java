package ru.pulsecore.app.modules.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.pulsecore.app.modules.player.service.analytic.*;
import ru.pulsecore.app.modules.shared.propirties.GigaChatProperties;

import java.util.List;

@Configuration
public class AgentConfig {
    @Bean
    public OpenRouterService openRouterService(GigaChatProperties props) {
        return new OpenRouterService(props);
    }

    @Bean
    public List<Agent> agents(FinanceAgent finance, LeagueAgent league, ComparisonAgent comparison) {
        return List.of(finance, league, comparison);
    }
}