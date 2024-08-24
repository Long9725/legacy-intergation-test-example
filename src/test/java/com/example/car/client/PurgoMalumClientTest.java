package com.example.car.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static org.assertj.core.api.Assertions.assertThat;

public class PurgoMalumClientTest {
    @ClassRule
    public static WireMockClassRule wireMockClassRule = new WireMockClassRule(WireMockConfiguration.wireMockConfig().dynamicPort());

    private static PurgoMalumClient purgoMalumClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeClass
    public static void setUp() {
        final String apiUrl = "http://localhost:" + wireMockClassRule.port();

        purgoMalumClient = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(PurgoMalumClient.class, apiUrl);
    }

    @AfterClass
    public static void teardown() {
        wireMockClassRule.stop();
    }

    @Test
    public void testFeignClient() throws JsonProcessingException {
        final String profanity = "this is some test fuck";
        final ProfanityJsonResponse response = ProfanityJsonResponse.builder()
                .result("this is some test ****")
                .build();

        wireMockClassRule.stubFor(WireMock.get(WireMock.urlPathEqualTo("/json"))
                .withQueryParam("text", equalTo(profanity))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(objectMapper.writeValueAsString(response))));

        final ProfanityJsonResponse actual = purgoMalumClient.profanityJson(profanity);

        assertThat(actual.getResult()).isNotEqualTo(profanity);
    }
}

