package com.example.car.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static org.assertj.core.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class PurgoMalumClientTest {
    private WireMockServer wireMockServer;
    private PurgoMalumClient purgoMalumClient;
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        apiUrl = "http://localhost:" + wireMockServer.port();

        purgoMalumClient = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(PurgoMalumClient.class, apiUrl);
    }

    @After
    public void teardown() {
        wireMockServer.stop();
    }

    @Test
    public void testFeignClient() throws JsonProcessingException {
        final String profanity = "this is some test fuck";
        final ProfanityJsonResponse response = ProfanityJsonResponse.builder()
                .result("this is some test ****")
                .build();

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/json"))
                .withQueryParam("text", equalTo(profanity))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(objectMapper.writeValueAsString(response))));

        final ProfanityJsonResponse actual = purgoMalumClient.profanityJson(profanity);

        assertThat(actual.getResult()).isNotEqualTo(profanity);
    }
}

