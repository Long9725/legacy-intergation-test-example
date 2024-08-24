package com.example.car.service;

import com.example.car.client.ProfanityFilterService;
import com.example.car.client.ProfanityJsonResponse;
import com.example.car.client.PurgoMalumClient;
import com.example.car.dao.CarDao;
import com.example.car.entity.Car;
import com.example.car.mapper.CarMapper;
import com.example.test.fixture.CarTestFixture;
import com.example.test.spring.IntegrationRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.test.spring.IntegrationRule.wireMockClassRule;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
@ContextConfiguration(classes = CarServiceParameterizedTestConfiguration.class)
public class CarServiceParameterizedTest extends IntegrationRule {
    private static final List<String> badwords = Arrays.asList("fuck", "bitch", "asshole", "damn");

    @Autowired
    private CarService carService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Parameterized.Parameter(0)
    public String goodCarName;

    @Parameterized.Parameter(1)
    public String badCarName;

    @Parameterized.Parameters(name = "{index}: Test with goodName={0}, badName={1}")
    public static Collection<Object[]> data() {
        final int size = 100;
        final List<String> goodCarNames = CarTestFixture.getNameStrings(size);
        final List<String> badCarNames = CarTestFixture.getBadWordNameStrings(badwords, size);

        return IntStream.range(0, size)
                .mapToObj(i -> new Object[]{goodCarNames.get(i), badCarNames.get(i)})
                .collect(Collectors.toList());
    }

    @Test
    public void createCarTest() throws JsonProcessingException {
        // given
        final String carName = goodCarName;

        // given - client
        final ProfanityJsonResponse response = ProfanityJsonResponse.builder()
                .result(carName)
                .build();

        wireMockClassRule.stubFor(WireMock.get(WireMock.urlPathEqualTo("/json"))
                .withQueryParam("text", equalTo(carName))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(objectMapper.writeValueAsString(response))));

        // when
        final Car actual = carService.createCar(carName);

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(actual.getName()).isEqualTo(carName);
    }

    @Test
    public void createCarExceptionTest() throws JsonProcessingException {
        // given
        final String carName = badCarName;

        // given - client
        final String filteredName = badwords.stream()
                .filter(carName::contains)
                .findFirst()
                .map(badword -> carName.replaceAll(badword, "*"))
                .orElseThrow(() -> new IllegalArgumentException("잘못된 테스트 케이스입니다."));
        final ProfanityJsonResponse response = ProfanityJsonResponse.builder()
                .result(filteredName)
                .build();

        wireMockClassRule.stubFor(WireMock.get(WireMock.urlPathEqualTo("/json"))
                .withQueryParam("text", equalTo(carName))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(objectMapper.writeValueAsString(response))));

        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> carService.createCar(carName));
    }
}

@Configuration
@ComponentScan(basePackageClasses = {
        CarService.class,
        CarDao.class,
        CarMapper.class,
        ProfanityFilterService.class,
})
class CarServiceParameterizedTestConfiguration {
    @Bean
    public PurgoMalumClient purgoMalumClient() {
        final String apiUrl = "http://localhost:" + wireMockClassRule.port();
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(PurgoMalumClient.class, apiUrl);
    }

    @Bean
    public DatabaseSchemaFactory databaseSchemaFactory() {
        final DatabaseSchemaFactory databaseSchemaFactory = mock(DatabaseSchemaFactory.class);

        doReturn(Optional.of("test_schema")).when(databaseSchemaFactory).getSchema();

        return databaseSchemaFactory;
    }
}