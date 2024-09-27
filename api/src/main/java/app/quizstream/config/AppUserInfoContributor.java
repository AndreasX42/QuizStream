package app.quizstream.config;

import lombok.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import app.quizstream.repository.UserRepository;

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
