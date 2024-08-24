package com.andreasx42.quizstreamapi.security.config;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvConfigs {

    public String appAddress;
    public String backendAddress;
    public String backendCreateNewQuizEndpoint;

    public final int TOKEN_EXPIRATION = 7200000; // 2 hours.
    public final String BEARER_PREFIX = "Bearer ";
    public final String AUTHORIZATION = "Authorization";
    public final String REGISTER_PATH = "/users/register";
    public final String AUTH_PATH = "/users/login";

    @PostConstruct
    public void init() {
        appAddress = String.format("http://%s", appHost);
        backendAddress = String.format("http://%s:%s", backendHost, backendPort);
        backendCreateNewQuizEndpoint = backendAddress + "/quizzes/new";
    }

    @Value("${jwt.secret}")
    public String jwtSecret;

    @Value("${app.host}")
    private String appHost;

    @Value("${app.port}")
    private String appPort;

    @Value("${backend.host}")
    private String backendHost;

    @Value("${backend.port}")
    private String backendPort;
}
