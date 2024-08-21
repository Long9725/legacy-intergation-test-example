package com.example.car.service;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DatabaseSchemaFactory {
    public Optional<String> getSchema() {
        return Optional.empty();
    }
}
