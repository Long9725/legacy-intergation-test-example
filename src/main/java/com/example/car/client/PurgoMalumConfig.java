package com.example.car.client;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PurgoMalumConfig {
    @Bean
    public PurgoMalumClient purgoMalumClient() {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(PurgoMalumClient.class, "https://www.purgomalum.com/service");
    }
}
