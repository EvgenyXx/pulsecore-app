package ru.pulsecore.app.modules.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.pulsecore.app.modules.player_modeles.application.auth.oauth.OAuth2SuccessHandler;
import ru.pulsecore.app.modules.shared.properties.SecurityProperties;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final SessionRestoreFilter sessionRestoreFilter;
    private final CustomLogoutSuccessHandler logoutSuccessHandler;
    private final SecurityProperties securityProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(sessionRestoreFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .securityContext(securityContext -> securityContext.requireExplicitSave(false))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(securityProperties.getMaximumSessions())
                        .maxSessionsPreventsLogin(false))
                .authorizeHttpRequests(auth -> {
                    securityProperties.getPublicUrls().forEach(url -> auth.requestMatchers(url).permitAll());
                    securityProperties.getAuthenticatedUrls().forEach(url -> auth.requestMatchers(url).authenticated());
                    securityProperties.getAdminUrls().forEach(url ->
                            auth.requestMatchers(url).hasAuthority(securityProperties.getAdminAuthority()));
                    auth.anyRequest().authenticated();
                })
                .oauth2Login(oauth2 -> oauth2
                        .loginPage(securityProperties.getLoginPage())
                        .successHandler(oAuth2SuccessHandler))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(logout -> logout
                        .logoutUrl(securityProperties.getLogoutUrl())
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .deleteCookies(securityProperties.getSessionCookieName()))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (request.getRequestURI().startsWith(securityProperties.getApiPathPrefix())) {
                                response.sendError(securityProperties.getApiErrorStatus(),
                                        securityProperties.getApiErrorMessage());
                            } else {
                                response.sendRedirect(securityProperties.getLoginPage());
                            }
                        }));

        return http.build();
    }
}