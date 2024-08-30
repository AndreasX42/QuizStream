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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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
        authFilter.setFilterProcessesUrl(envConfigs.AUTH_PATH);

        http.cors()
                .and()
                .headers(headers -> headers.frameOptions()
                        .disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/h2/**")
                        .permitAll()
                        .requestMatchers("todos/**")
                        .permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                                "/swagger-ui.html", "/swagger-resources/**",
                                "/webjars/**")
                        .permitAll() // Allow access to Swagger UI and API documentation
                        .requestMatchers(HttpMethod.POST, envConfigs.REGISTER_PATH)
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
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(envConfigs.getAppHost());
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}