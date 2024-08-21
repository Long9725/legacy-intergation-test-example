package com.example.car.client;

import feign.Param;
import feign.RequestLine;

public interface PurgoMalumClient {
    @RequestLine("GET /json?text={text}")
    ProfanityJsonResponse profanityJson(@Param("text") String text);
}
