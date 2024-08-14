package com.andreasx42.quizstreamapi.security.manager;

import java.util.Collections;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.andreasx42.quizstreamapi.entity.User;
import com.andreasx42.quizstreamapi.service.api.IUserService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private IUserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        User user = userService.getByName(authentication.getName());

        if (!bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new BadCredentialsException("INCORRECT PASSWORD");
        }

        Set<SimpleGrantedAuthority> authority = Collections
                .singleton(new SimpleGrantedAuthority(user.getRole().toString()));

        org.springframework.security.core.userdetails.User userDetails = new CustomUserDetails(user.getId(),
                user.getUsername(), user.getPassword(),
                authority);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authority);
    }

}
