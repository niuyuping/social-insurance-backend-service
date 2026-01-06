package jp.asatex.niuyuping.social_insurance_backend_service.controller;

import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import jp.asatex.niuyuping.social_insurance_backend_service.application.PremiumBracketApplicationService;
import jp.asatex.niuyuping.social_insurance_backend_service.application.dto.SocialInsuranceApplicationDto;
import jp.asatex.niuyuping.social_insurance_backend_service.application.dto.SocialInsuranceApplicationDto.EmployeeCost;
import jp.asatex.niuyuping.social_insurance_backend_service.application.dto.SocialInsuranceApplicationDto.EmployerCost;
import jp.asatex.niuyuping.social_insurance_backend_service.controller.dto.SocialInsuranceDto;
import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;

@WebFluxTest(PremiumBracketController.class)
class SocialInsuranceControllerTest {

    
    @Autowired
    private WebTestClient webTestClient;
    
    @MockitoBean
    private PremiumBracketApplicationService premiumBracketApplicationService;

    @Test
    void testSocialInsuranceQuery_AgeOver40() {
      Mockito.when(premiumBracketApplicationService.socialInsuranceQuery(550000, 45))
        .thenReturn(Mono.just(SocialInsuranceApplicationDto.builder()
          .employeeCost(EmployeeCost.builder()
            .healthCostWithNoCare(BigDecimal.valueOf(27776.00))
            .careCost(BigDecimal.valueOf(4452.00))
            .pension(BigDecimal.valueOf(51240.00))
            .build())
          .employerCost(EmployerCost.builder()
            .healthCostWithNoCare(BigDecimal.valueOf(27776.00))
            .careCost(BigDecimal.valueOf(4452.00))
            .pension(BigDecimal.valueOf(51240.00))
            .build())
          .build()));

      webTestClient
        .get()
        .uri("/socialInsuranceQuery?monthlySalary=550000&age=45")
        .exchange()
        .expectStatus()
        .isOk().expectBody(SocialInsuranceDto.class)
        .value(socialInsuranceDto -> {
            assertThat(
                socialInsuranceDto.getEmployeeCost().getHealthCostWithNoCare()
              ).isEqualByComparingTo(new BigDecimal("27776.00"));
            assertThat(
                socialInsuranceDto.getEmployeeCost().getCareCost()
              ).isEqualByComparingTo(new BigDecimal("4452.00"));
            assertThat(
                socialInsuranceDto.getEmployeeCost().getPension()
              ).isEqualByComparingTo(new BigDecimal("51240.00"));
            assertThat(
                socialInsuranceDto.getEmployerCost().getHealthCostWithNoCare()
              ).isEqualByComparingTo(new BigDecimal("27776.00"));
            assertThat(
                socialInsuranceDto.getEmployerCost().getCareCost()
              ).isEqualByComparingTo(new BigDecimal("4452.00"));
            assertThat(
                socialInsuranceDto.getEmployerCost().getPension()
              ).isEqualByComparingTo(new BigDecimal("51240.00"));
        });
    }

    @Test
    void testSocialInsuranceQuery_AgeLessThan40() {
      Mockito.when(premiumBracketApplicationService.socialInsuranceQuery(280000, 25))
        .thenReturn(Mono.just(SocialInsuranceApplicationDto.builder()
          .employeeCost(EmployeeCost.builder()
            .healthCostWithNoCare(BigDecimal.valueOf(13888.00))
            .careCost(BigDecimal.valueOf(0.00))
            .pension(BigDecimal.valueOf(25620.00))
            .build())
          .employerCost(EmployerCost.builder()
            .healthCostWithNoCare(BigDecimal.valueOf(13888.00))
            .careCost(BigDecimal.valueOf(0.00))
            .pension(BigDecimal.valueOf(25620.00))
            .build())
          .build()));

      webTestClient
        .get()
        .uri("/socialInsuranceQuery?monthlySalary=280000&age=25")
        .exchange()
        .expectStatus()
        .isOk().expectBody(SocialInsuranceDto.class)
        .value(socialInsuranceDto -> {
            assertThat(
                socialInsuranceDto.getEmployeeCost().getHealthCostWithNoCare()
              ).isEqualByComparingTo(new BigDecimal("13888.00"));
            assertThat(
                socialInsuranceDto.getEmployeeCost().getCareCost()
              ).isEqualByComparingTo(new BigDecimal("0.00"));
            assertThat(
                socialInsuranceDto.getEmployeeCost().getPension()
              ).isEqualByComparingTo(new BigDecimal("25620.00"));
            assertThat(
                socialInsuranceDto.getEmployerCost().getHealthCostWithNoCare()
              ).isEqualByComparingTo(new BigDecimal("13888.00"));
            assertThat(
                socialInsuranceDto.getEmployerCost().getCareCost()
              ).isEqualByComparingTo(new BigDecimal("0.00"));
            assertThat(
                socialInsuranceDto.getEmployerCost().getPension()
              ).isEqualByComparingTo(new BigDecimal("25620.00"));
        });
    }

    @Test
    void testSocialInsuranceQuery_IsNotFound() {
      Mockito.when(premiumBracketApplicationService.socialInsuranceQuery(1050000, 30))
        .thenReturn(Mono.error(new IllegalArgumentException("未找到月薪 1050000 对应的保险费等级")));

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
