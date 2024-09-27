package app.quizstream.security.manager;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import app.quizstream.entity.User;
import app.quizstream.service.UserService;

import java.util.Collections;
import java.util.Set;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        User user = userService.getByUserName(authentication.getName());

        if (!bCryptPasswordEncoder.matches(authentication.getCredentials()
                .toString(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }

        Set<SimpleGrantedAuthority> authority = Collections
                .singleton(new SimpleGrantedAuthority(user.getRole()
                        .toString()));

        org.springframework.security.core.userdetails.User userDetails = new CustomUserDetails(user.getId(),
                user.getUsername(), user.getPassword(), user.getEmail(), authority);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authority);
    }

}
