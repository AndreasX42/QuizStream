package com.andreasx42.quizstreamapi;

import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.ForwardedHeaderFilter;

@EnableAsync
@AllArgsConstructor
@SpringBootApplication
public class QuizStreamApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizStreamApplication.class, args);
    }

    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

}