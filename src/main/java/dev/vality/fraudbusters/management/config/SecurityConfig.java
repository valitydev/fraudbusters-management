package dev.vality.fraudbusters.management.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(value = "keycloak.enabled", matchIfMissing = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/**/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/**/prometheus").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .requireCsrfProtectionMatcher(new KeycloakCsrfRequestMatcher()) // Свой матчер
                )
                .cors(AbstractHttpConfigurer::disable) // Или настрой нужные origin
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // Если нужно
                );

        return http.build();
    }

    static class KeycloakCsrfRequestMatcher implements RequestMatcher {
        @Override
        public boolean matches(HttpServletRequest request) {
            return false;
        }
    }

    static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

}

