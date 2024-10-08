package app.quizstream.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.quizstream.dto.auth.LoginResponseDto;
import app.quizstream.entity.User;
import app.quizstream.security.config.EnvConfigs;
import app.quizstream.security.manager.CustomUserDetails;
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
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {

        String role = authResult.getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid Role"))
                .toString();

        String token = JWT.create()
                .withSubject(authResult.getName())
                .withExpiresAt(new Date(System.currentTimeMillis() + envConfigs.TOKEN_EXPIRATION))
                .withClaim("role", role)
                .sign(Algorithm.HMAC512(envConfigs.getJwtSecret()));

        response.addHeader(envConfigs.AUTHORIZATION, envConfigs.BEARER_PREFIX + token);

        // Create a response object user data
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

        LoginResponseDto loginResponse = new LoginResponseDto(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                role);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), loginResponse);
    }
}
