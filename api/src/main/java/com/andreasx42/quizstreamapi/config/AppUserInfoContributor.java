package com.andreasx42.quizstreamapi.config;

import com.andreasx42.quizstreamapi.repository.UserRepository;
import lombok.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Value
public class AppUserInfoContributor implements InfoContributor {

    UserRepository userRepository;

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("app-user.stats", Map.of("count", userRepository.count()))
                .build();

    }
}
