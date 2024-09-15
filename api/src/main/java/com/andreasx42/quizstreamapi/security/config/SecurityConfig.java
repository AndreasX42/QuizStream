package com.andreasx42.quizstreamapi.security.config;

import com.andreasx42.quizstreamapi.security.filter.AuthenticationFilter;
import com.andreasx42.quizstreamapi.security.filter.ExceptionHandlerFilter;
import com.andreasx42.quizstreamapi.security.filter.JWTAuthorizationFilter;
import com.andreasx42.quizstreamapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@AllArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private AuthenticationManager authenticationManager;
    private UserService userService;
    private EnvConfigs envConfigs;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationFilter authFilter = new AuthenticationFilter(authenticationManager, envConfigs);
        authFilter.setFilterProcessesUrl(envConfigs.AUTH_PATH_FRONTEND);

        http
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .headers(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/swagger-ui/**", "/api/v1/openapi/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, envConfigs.REGISTER_PATH)
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, envConfigs.AUTH_PATH_API)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
                .addFilter(authFilter)
                .addFilterAfter(new JWTAuthorizationFilter(userService, envConfigs), AuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(envConfigs.getAppHost());
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}