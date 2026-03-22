package com.instantservices.backend.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                // ✅ CORS ENABLE
                .cors(cors -> {})

                .authorizeHttpRequests(auth -> auth
                        // PUBLIC ENDPOINTS
                        .requestMatchers("/api/auth/**").permitAll()
                        // ✅ ROLE BASED
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // PROTECTED ENDPOINTS
                        .requestMatchers("/api/tasks/**").authenticated()
                        .requestMatchers("/api/offers/**").authenticated()

                        // EVERYTHING ELSE
                        .anyRequest().authenticated()
                )
                // ✅ EXCEPTION HANDLING
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.getWriter().write("Unauthorized");
                        })
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        // Register JWT filter BEFORE username/password auth filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
