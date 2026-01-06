package jp.asatex.niuyuping.social_insurance_backend_service;

import static org.assertj.core.api.Assertions.assertThat;

import jp.asatex.niuyuping.social_insurance_backend_service.controller.dto.SocialInsuranceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SocialInsuranceBackendServiceApplicationTests {

  @Container
  @ServiceConnection
  private static final PostgreSQLContainer<?> postgres =
    new PostgreSQLContainer<>("postgres:17.4");

  @LocalServerPort
  private int port;

  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    webTestClient = WebTestClient.bindToServer()
        .baseUrl("http://localhost:" + port)
        .build();
  }

  @Test
  void contextLoads() {}

  @Test
  void testSocialInsuranceQuery_AgeOver40() {
    webTestClient
      .get()
      .uri("/socialInsuranceQuery?monthlySalary=550000&age=45")
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody(SocialInsuranceDto.class)
      .value(socialInsuranceDto -> {
        assertThat(
          socialInsuranceDto.getEmployeeCost().getHealthCostWithNoCare()
        ).isEqualByComparingTo(new BigDecimal("27776.00"));
        assertThat(
          socialInsuranceDto.getEmployeeCost().getCareCost()
        ).isEqualByComparingTo(new BigDecimal("4452.00"));
        assertThat(socialInsuranceDto.getEmployeeCost().getPension()).isEqualTo(
          new BigDecimal("51240.00")
        );
        assertThat(
          socialInsuranceDto.getEmployerCost().getHealthCostWithNoCare()
        ).isEqualByComparingTo(new BigDecimal("27776.00"));
        assertThat(
          socialInsuranceDto.getEmployerCost().getCareCost()
        ).isEqualByComparingTo(new BigDecimal("4452.00"));
        assertThat(socialInsuranceDto.getEmployerCost().getPension()).isEqualTo(
          new BigDecimal("51240.00")
        );
      });
  }

  @Test
  void testSocialInsuranceQuery_AgeLessThan40() {
    webTestClient
      .get()
      .uri("/socialInsuranceQuery?monthlySalary=280000&age=25")
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody(SocialInsuranceDto.class)
      .value(socialInsuranceDto -> {
        assertThat(
          socialInsuranceDto.getEmployeeCost().getHealthCostWithNoCare()
        ).isEqualByComparingTo(new BigDecimal("13888.00"));
        assertThat(
          socialInsuranceDto.getEmployeeCost().getCareCost()
        ).isEqualByComparingTo(new BigDecimal("0.00"));
        assertThat(socialInsuranceDto.getEmployeeCost().getPension()).isEqualTo(
          new BigDecimal("25620.00")
        );
        assertThat(
          socialInsuranceDto.getEmployerCost().getHealthCostWithNoCare()
        ).isEqualByComparingTo(new BigDecimal("13888.00"));
        assertThat(
          socialInsuranceDto.getEmployerCost().getCareCost()
        ).isEqualByComparingTo(new BigDecimal("0.00"));
        assertThat(socialInsuranceDto.getEmployerCost().getPension()).isEqualTo(
          new BigDecimal("25620.00")
        );
      });
  }

  @Test
  void testSocialInsuranceQuery_IsNotFound() {
    webTestClient
      .get()
      .uri("/socialInsuranceQuery?monthlySalary=1050000&age=30")
      .exchange()
      .expectStatus()
      .isBadRequest()
      .expectBody(String.class)
      .value(responseBody -> {
        assertThat(responseBody).contains("未找到月薪 1050000 对应的保险费等级");
      });
  }
}
