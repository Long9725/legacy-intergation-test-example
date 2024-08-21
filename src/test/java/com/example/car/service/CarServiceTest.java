package com.example.car.service;

import com.example.car.client.ProfanityFilterClient;
import com.example.car.client.ProfanityFilterService;
import com.example.car.client.ProfanityJsonResponse;
import com.example.car.client.PurgoMalumClient;
import com.example.car.dao.CarDao;
import com.example.car.entity.Car;
import com.example.car.mapper.CarMapper;
import com.example.test.config.MyBatisIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ContextConfiguration(classes = CarServiceTestConfiguration.class)
public class CarServiceTest extends MyBatisIntegrationTest {
    @Autowired
    private  CarService carService;

    @Autowired
    private  WireMockServer wireMockServer;

    private static WireMockServer wireMockServerForStop;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUpClass() {
        if(wireMockServerForStop == null) {
            wireMockServerForStop = wireMockServer;
        }
    }

    @AfterClass
    public static void teardownClass() {
        if(wireMockServerForStop != null) {
            wireMockServerForStop.stop();
        }
    }


    @Test
    public void createCarTest() throws JsonProcessingException {
        // given
        final String carName = "This is test car name";

        // given - client
        final ProfanityJsonResponse response = ProfanityJsonResponse.builder()
                .result("This is test car name")
                .build();

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/json"))
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
        final String carName = "This is test car name fuck";

        // given - client
        final ProfanityJsonResponse response = ProfanityJsonResponse.builder()
                .result("This is test car name ****")
                .build();

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/json"))
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
class CarServiceTestConfiguration {
    @Bean
    public WireMockServer wireMockServer() {
        final WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());

        wireMockServer.start();

        return wireMockServer;
    }

    @Bean
    public PurgoMalumClient purgoMalumClient(final WireMockServer wireMockServer) {
        final String apiUrl = "http://localhost:" + wireMockServer.port();
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