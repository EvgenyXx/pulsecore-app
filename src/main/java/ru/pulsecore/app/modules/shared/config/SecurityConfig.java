// SecurityConfig.java
package ru.pulsecore.app.modules.shared.config;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.pulsecore.app.modules.shared.SessionProperties;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SessionProperties sessionProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html", "/register.html",
                                "/subscribe.html", "/profile.html", "/dashboard.html",
                                "/img.png", "/api/auth/**", "/api/payment/webhook",
                                "/api/player/**"
                        ).permitAll()
                        .requestMatchers("/admin.html", "/api/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            Cookie cookie = new Cookie(sessionProperties.getName(), null);
                            cookie.setPath("/");
                            cookie.setHttpOnly(true);
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                            response.setStatus(200);
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies(sessionProperties.getName())
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            Cookie cookie = new Cookie(sessionProperties.getName(), null);
                            cookie.setPath("/");
                            cookie.setHttpOnly(true);
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                            response.sendRedirect("/");
                        })
                );

        return http.build();
    }
}