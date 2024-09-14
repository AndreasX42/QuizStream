package com.andreasx42.quizstreamapi.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public final class Util {

    public static void assertThatIsValidJwtToken(MockHttpServletResponse response, String username, String role, String bearer_prefix, String jwtSecret, int token_expiration) {
        String jwtToken = response.getHeader("Authorization");

        assertThat(jwtToken).isNotNull();
        assertThat(jwtToken).startsWith(bearer_prefix);
        assertThat(jwtToken.substring(7)).matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");
        
        String token = jwtToken.replace(bearer_prefix, "");

        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(jwtSecret))
                .build()
                .verify(token);

        // check header
        assertThat(jwt.getType()).isEqualTo("JWT");
        assertThat(jwt.getAlgorithm()).isEqualTo("HS512");

        // check body
        assertThat(jwt.getSubject()).isEqualTo(username);
        assertThat(jwt.getClaim("role")
                .asString()).isEqualTo(role);
        assertThat(jwt.getExpiresAt()).isBetween(
                new Date(System.currentTimeMillis() + 2 * 58 * 60 * 1000),
                new Date(System.currentTimeMillis() + token_expiration));

    }

}
