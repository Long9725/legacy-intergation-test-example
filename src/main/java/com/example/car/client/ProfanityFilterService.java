package com.example.car.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfanityFilterService implements ProfanityFilterClient {
    private final PurgoMalumClient purgoMalumClient;

    @Override
    public boolean isCleanText(String text) {
        final ProfanityJsonResponse response = purgoMalumClient.profanityJson(text);
        return text.equals(response.getResult());
    }

    @Override
    public boolean isNotCleanText(String text) {
        return !isCleanText(text);
    }
}
