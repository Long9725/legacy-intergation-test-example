package com.example.car.client;

public interface ProfanityFilterClient {
    boolean isCleanText(final String text);

    boolean isNotCleanText(final String text);
}
