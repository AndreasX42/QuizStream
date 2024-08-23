package com.andreasx42.quizstreamapi.security.filter;

import com.andreasx42.quizstreamapi.dto.auth.LoginResponseDto;
import com.andreasx42.quizstreamapi.entity.User;
import com.andreasx42.quizstreamapi.security.config.EnvConfigs;
import com.andreasx42.quizstreamapi.security.manager.CustomUserDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

@AllArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private EnvConfigs envConfigs;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(),
                    user.getPassword());

            return authenticationManager.authenticate(authentication);

        } catch (IOException ex) {
            throw new RuntimeException();
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {


        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter()
                .write(failed.getMessage());
        response.getWriter()
                .flush();
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        String role = authResult.getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("INVALID ROLE"))
                .toString();

        String token = JWT.create()
                .withSubject(authResult.getName())
                .withExpiresAt(new Date(System.currentTimeMillis() + envConfigs.TOKEN_EXPIRATION))
                .withClaim("role", role)
                .sign(Algorithm.HMAC512(envConfigs.jwtSecret));

        response.addHeader(envConfigs.AUTHORIZATION, envConfigs.BEARER_PREFIX + token);

        // Create a response object with token and user data
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

        LoginResponseDto loginResponse = new LoginResponseDto(
                token,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail()
        );

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), loginResponse);
    }
}
