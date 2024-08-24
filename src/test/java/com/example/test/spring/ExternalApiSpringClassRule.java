package com.example.test.spring;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

public abstract class ExternalApiSpringClassRule {
    @ClassRule
    public static final SpringClassRule springClassRule = new SpringClassRule();

    @ClassRule
    public static WireMockClassRule wireMockClassRule = new WireMockClassRule(WireMockConfiguration.wireMockConfig().dynamicPort());

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
}
