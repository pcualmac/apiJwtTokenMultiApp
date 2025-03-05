package com.example.apiJwtToken.config;

import com.example.apiJwtToken.security.JwtAuthenticationFilter;
import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final ApplicationService applicationService;
    private AuthenticationProvider authenticationProvider;

    public SecurityConfig(CustomUserDetailsService userDetailsService, ApplicationService applicationService) {
        this.userDetailsService = userDetailsService;
        this.applicationService = applicationService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> {
                // Dynamically build permitAll matchers based on application names
                List<String> permitAllMatchers = applicationService.getAllApplications().stream()
                    .flatMap(app -> List.of(
                        "/api/auth/" + app.getApplicationName() + "/register",
                        "/api/auth/" + app.getApplicationName() + "/login" //Add login also to permit all
                    ).stream())
                    .collect(Collectors.toList());

                permitAllMatchers.add("/api/auth/login");
                permitAllMatchers.add("/error");

                auth.requestMatchers(permitAllMatchers.toArray(new String[0])).permitAll();

                // Dynamically build request matchers based on application names
                List<RequestMatcher> applicationAuthMatchers = applicationService.getAllApplications().stream()
                    .map(app -> new AntPathRequestMatcher("/api/auth/" + app.getApplicationName() + "/**"))
                    .collect(Collectors.toList());

                applicationAuthMatchers.forEach(auth::requestMatchers);

                auth.anyRequest().authenticated();
            })
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                })
                .deleteCookies("JSESSIONID"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityContextLogoutHandler logoutHandler() {
        return new SecurityContextLogoutHandler();
    }
}
