package com.example.car.client;

import com.example.test.fixture.CarTestFixture;
import com.example.test.spring.ExternalApiSpringClassRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
@ContextConfiguration(classes = {ProfanityFilterServiceTestConfig.class})
public class ProfanityFilterServiceTest extends ExternalApiSpringClassRule {
    private static final List<String> badwords = Arrays.asList("fuck", "bitch", "asshole", "damn");

    @Autowired
    private ProfanityFilterService profanityFilterService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Parameterized.Parameter(0)
    public String goodword;

    @Parameterized.Parameter(1)
    public String badword;

    @Parameterized.Parameters(name = "{index} - goodword: {0}, badword: {1}")
    public static List<Object[]> testCases() {
        final int size = 100;
        final List<String> goodwordTestcases = CarTestFixture.getNameStrings(size);
        final List<String> badwordTestcases = CarTestFixture.getBadWordNameStrings(badwords, size);

        return IntStream.range(0, size)
                .mapToObj(i -> new Object[]{goodwordTestcases.get(i), badwordTestcases.get(i)})
                .collect(Collectors.toList());
    }

    @Test
    public void isCleanText() throws JsonProcessingException {
        // given
        final String text = goodword;

        // given - client
        final ProfanityJsonResponse response = ProfanityJsonResponse.builder()
                .result(text)
                .build();

        wireMockClassRule.stubFor(WireMock.get(WireMock.urlPathEqualTo("/json"))
                .withQueryParam("text", equalTo(text))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(objectMapper.writeValueAsString(response))));

        final boolean actual = profanityFilterService.isCleanText(text);

        assertThat(actual).isTrue();
    }

    @Test
    public void isNotCleanText() throws JsonProcessingException {
        final String profanity = badword;
        final String filteredName = badwords.stream()
                .filter(profanity::contains)
                .findFirst()
                .map(badword -> profanity.replaceAll(badword, "*"))
                .orElseThrow(() -> new IllegalArgumentException("잘못된 테스트 케이스입니다."));
        final ProfanityJsonResponse response = ProfanityJsonResponse.builder()
                .result(filteredName)
                .build();

        wireMockClassRule.stubFor(WireMock.get(WireMock.urlPathEqualTo("/json"))
                .withQueryParam("text", equalTo(profanity))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(objectMapper.writeValueAsString(response))));

        final boolean actual = profanityFilterService.isNotCleanText(profanity);

        assertThat(actual).isTrue();
    }
}

@Configuration
@ComponentScan(basePackageClasses = {
        ProfanityFilterService.class
})
class ProfanityFilterServiceTestConfig {
    @Bean
    public PurgoMalumClient purgoMalumClient(final WireMockServer wireMockServer) {
        final String apiUrl = "http://localhost:" + wireMockServer.port();
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(PurgoMalumClient.class, apiUrl);
    }
}