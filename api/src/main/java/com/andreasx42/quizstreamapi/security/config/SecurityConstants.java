package com.andreasx42.quizstreamapi.security.config;

public class SecurityConstants {
    public static final int TOKEN_EXPIRATION = 7200000; // 7200000 milliseconds = 7200 seconds = 2 hours.
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";
    public static final String REGISTER_PATH = "/users/register";
    public static final String AUTH_PATH = "/users/login";

}
