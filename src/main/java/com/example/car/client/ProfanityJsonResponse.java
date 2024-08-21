package com.example.car.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProfanityJsonResponse {
    private final String result;

    @Builder(toBuilder = true)
    public ProfanityJsonResponse(@JsonProperty("result") final String result) {
        this.result = result;
    }
}
