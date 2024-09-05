package com.andreasx42.quizstreamapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.util.Collections;

@Configuration
public class OpenApiConfig {

    private final Environment environment;

    public OpenApiConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    OpenAPI openApi() {
        var openAPI = new OpenAPI()
                .info(new Info()
                        .title("QuizStream API")
                        .description("API that manages the QuizStream app")
                        .version("v0.1"));

        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            openAPI.setServers(Collections.singletonList(new Server().url("/api/v1")));
        }

        return openAPI;
    }

}
