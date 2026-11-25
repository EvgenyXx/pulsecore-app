package ru.pulsecore.app.player.infrastructure.config;

import org.springframework.http.HttpMethod;
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
//import ru.pulsecore.app.player.application.auth.oauth.OAuth2SuccessHandler;
import ru.pulsecore.app.player.infrastructure.filter.SessionRestoreFilter;
import ru.pulsecore.app.player.infrastructure.properties.SecurityProperties;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

//    private final OAuth2SuccessHandler oAuth2SuccessHandler;
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
                        .maximumSessions(securityProperties.maximumSessions())
                        .maxSessionsPreventsLogin(false))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/player/register").hasAuthority(securityProperties.adminAuthority());
                    auth.requestMatchers(HttpMethod.POST, "/api/player/oauth-finish").hasAuthority(securityProperties.adminAuthority());
                    securityProperties.publicUrls().forEach(url -> auth.requestMatchers(url).permitAll());
                    securityProperties.authenticatedUrls().forEach(url -> auth.requestMatchers(url).authenticated());
                    securityProperties.adminUrls().forEach(url ->
                            auth.requestMatchers(url).hasAuthority(securityProperties.adminAuthority()));
                    auth.anyRequest().authenticated();
                })
//                .oauth2Login(oauth2 -> oauth2
//                        .loginPage(securityProperties.loginPage())
//                        .successHandler(oAuth2SuccessHandler))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(logout -> logout
                        .logoutUrl(securityProperties.logoutUrl())
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .deleteCookies(securityProperties.sessionCookieName()))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (request.getRequestURI().startsWith(securityProperties.apiPathPrefix())) {
                                response.sendError(securityProperties.apiErrorStatus(),
                                        securityProperties.apiErrorMessage());
                            } else {
                                response.sendRedirect(securityProperties.loginPage());
                            }
                        }));

        return http.build();
    }
}