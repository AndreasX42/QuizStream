package app.quizstream.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import app.quizstream.entity.User;
import app.quizstream.security.config.EnvConfigs;
import app.quizstream.security.manager.CustomUserDetails;
import app.quizstream.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@AllArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private UserService userService;
    private EnvConfigs envConfigs;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header==null || !header.startsWith(envConfigs.BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.replace(envConfigs.BEARER_PREFIX, "");

        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(envConfigs.getJwtSecret()))
                .build()
                .verify(token);

        String username = jwt.getSubject();

        String role = jwt.getClaim("role")
                .asString();

        if (role==null) {
            filterChain.doFilter(request, response);
            return;
        }

        User user = userService.getByUserName(username);

        Set<SimpleGrantedAuthority> authority = Collections
                .singleton(new SimpleGrantedAuthority(user.getRole()
                        .toString()));

        org.springframework.security.core.userdetails.User userDetails = new CustomUserDetails(user.getId(),
                user.getUsername(), user.getPassword(), user.getEmail(), authority);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                authority);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response);

    }

}
