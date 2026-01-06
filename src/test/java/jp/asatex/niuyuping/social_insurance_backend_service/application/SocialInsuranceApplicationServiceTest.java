package jp.asatex.niuyuping.social_insurance_backend_service.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import jp.asatex.niuyuping.social_insurance_backend_service.domain.PremiumBracketDomainService;
import jp.asatex.niuyuping.social_insurance_backend_service.domain.dto.SocialInsuranceDomainDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest(classes = PremiumBracketApplicationService.class)
public class SocialInsuranceApplicationServiceTest {

  @Autowired
  private PremiumBracketApplicationService premiumBracketApplicationService;

  @MockitoBean
  private PremiumBracketDomainService premiumBracketDomainService;

  @Test
  void testSocialInsuranceApplicationService_AgeOver40() {
    Mockito.when(
      premiumBracketDomainService.socialInsuranceQuery(550000, 45)
    ).thenReturn(
      Mono.just(
        SocialInsuranceDomainDto.builder()
          .employeeCost(
            SocialInsuranceDomainDto.EmployeeCost.builder()
              .healthCostWithNoCare(BigDecimal.valueOf(27776.00))
              .careCost(BigDecimal.valueOf(4452.00))
              .pension(BigDecimal.valueOf(51240.00))
              .build()
          )
          .employerCost(
            SocialInsuranceDomainDto.EmployerCost.builder()
              .healthCostWithNoCare(BigDecimal.valueOf(27776.00))
              .careCost(BigDecimal.valueOf(4452.00))
              .pension(BigDecimal.valueOf(51240.00))
              .build()
          )
          .build()
      )
    );

    StepVerifier.create(
      premiumBracketApplicationService.socialInsuranceQuery(550000, 45)
    )
      .assertNext(socialInsuranceApplicationDto -> {
        assertThat(
          socialInsuranceApplicationDto
            .getEmployeeCost()
            .getHealthCostWithNoCare()
        ).isEqualByComparingTo(new BigDecimal("27776.00"));
        assertThat(
          socialInsuranceApplicationDto.getEmployeeCost().getCareCost()
        ).isEqualByComparingTo(new BigDecimal("4452.00"));
        assertThat(
          socialInsuranceApplicationDto.getEmployeeCost().getPension()
        ).isEqualByComparingTo(new BigDecimal("51240.00"));
        assertThat(
          socialInsuranceApplicationDto
            .getEmployerCost()
            .getHealthCostWithNoCare()
        ).isEqualByComparingTo(new BigDecimal("27776.00"));
        assertThat(
          socialInsuranceApplicationDto.getEmployerCost().getCareCost()
        ).isEqualByComparingTo(new BigDecimal("4452.00"));
        assertThat(
          socialInsuranceApplicationDto.getEmployerCost().getPension()
        ).isEqualByComparingTo(new BigDecimal("51240.00"));
      })
      .verifyComplete();
  }

  @Test
  void testSocialInsuranceApplicationService_AgeLessThan40() {
    Mockito.when(
      premiumBracketDomainService.socialInsuranceQuery(280000, 25)
    ).thenReturn(
      Mono.just(
        SocialInsuranceDomainDto.builder()
          .employeeCost(
            SocialInsuranceDomainDto.EmployeeCost.builder()
              .healthCostWithNoCare(BigDecimal.valueOf(13888.00))
              .careCost(BigDecimal.valueOf(0.00))
              .pension(BigDecimal.valueOf(25620.00))
              .build()
          )
          .employerCost(
            SocialInsuranceDomainDto.EmployerCost.builder()
              .healthCostWithNoCare(BigDecimal.valueOf(13888.00))
              .careCost(BigDecimal.valueOf(0.00))
              .pension(BigDecimal.valueOf(25620.00))
              .build()
          )
          .build()
      )
    );

    StepVerifier.create(
      premiumBracketApplicationService.socialInsuranceQuery(280000, 25)
    )
      .assertNext(socialInsuranceApplicationDto -> {
        assertThat(
          socialInsuranceApplicationDto
            .getEmployeeCost()
            .getHealthCostWithNoCare()
        ).isEqualByComparingTo(new BigDecimal("13888.00"));
        assertThat(
          socialInsuranceApplicationDto.getEmployeeCost().getCareCost()
        ).isEqualByComparingTo(new BigDecimal("0.00"));
        assertThat(
          socialInsuranceApplicationDto.getEmployeeCost().getPension()
        ).isEqualByComparingTo(new BigDecimal("25620.00"));
        assertThat(
          socialInsuranceApplicationDto
            .getEmployerCost()
            .getHealthCostWithNoCare()
        ).isEqualByComparingTo(new BigDecimal("13888.00"));
        assertThat(
          socialInsuranceApplicationDto.getEmployerCost().getCareCost()
        ).isEqualByComparingTo(new BigDecimal("0.00"));
        assertThat(
          socialInsuranceApplicationDto.getEmployerCost().getPension()
        ).isEqualByComparingTo(new BigDecimal("25620.00"));
      })
      .verifyComplete();
  }

  @Test
  void testSocialInsuranceApplicationService_IsNotFound() {
    Mockito.when(
      premiumBracketDomainService.socialInsuranceQuery(1050000, 30)
    ).thenReturn(
      Mono.error(
        new IllegalArgumentException("未找到月薪 1050000 对应的保险费等级")
      )
    );

    StepVerifier.create(
      premiumBracketApplicationService.socialInsuranceQuery(1050000, 30)
    )
      .expectErrorMatches(
        throwable ->
          throwable instanceof IllegalArgumentException &&
          throwable.getMessage().contains("未找到月薪 1050000 对应的保险费等级")
      )
      .verify();
  }
}
