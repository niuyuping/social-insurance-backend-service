package jp.asatex.niuyuping.social_insurance_backend_service.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import jp.asatex.niuyuping.social_insurance_backend_service.entity.PremiumBracket;
import jp.asatex.niuyuping.social_insurance_backend_service.repository.PremiumBracketRepository;
import jp.asatex.niuyuping.social_insurance_backend_service.repository.impl.PremiumBracketRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@SpringBootTest(classes = PremiumBracketDomainService.class)
public class SocialInsuranceDomainServiceTest {

  @Autowired
  private PremiumBracketDomainService premiumBracketDomainService;

  @MockitoBean
  private PremiumBracketRepository premiumBracketRepository;

  @MockitoBean
  private PremiumBracketRepositoryImpl premiumBracketRepositoryImpl;

  @Test
  void testSocialInsuranceDomainService_AgeOver40() {
    PremiumBracket mockBracket = PremiumBracket.builder()
      .id(1L)
      .grade("32(29)")
      .stdRem(560000)
      .minAmount(545000)
      .maxAmount(575000)
      .healthNoCare(new BigDecimal("55552.00"))
      .healthCare(new BigDecimal("64456.00"))
      .pension(new BigDecimal("102480.00"))
      .build();

    // Mock Repository 的返回值
    Mockito.when(premiumBracketRepository.findByAmount(550000)).thenReturn(
      Mono.just(mockBracket)
    );

    // 使用 StepVerifier 测试响应式流
    StepVerifier.create(
      premiumBracketDomainService.socialInsuranceQuery(550000, 45)
    )
      .assertNext(result -> {
        assertThat(
          result.getEmployeeCost().getHealthCostWithNoCare()
        ).isEqualByComparingTo(new BigDecimal("27776.00"));
        assertThat(result.getEmployeeCost().getCareCost()).isEqualByComparingTo(
          new BigDecimal("4452.00")
        );
        assertThat(result.getEmployeeCost().getPension()).isEqualByComparingTo(
          new BigDecimal("51240.00")
        );

        assertThat(
          result.getEmployerCost().getHealthCostWithNoCare()
        ).isEqualByComparingTo(new BigDecimal("27776.00"));
        assertThat(result.getEmployerCost().getCareCost()).isEqualByComparingTo(
          new BigDecimal("4452.00")
        );
        assertThat(result.getEmployerCost().getPension()).isEqualByComparingTo(
          new BigDecimal("51240.00")
        );
      })
      .verifyComplete();
  }

  @Test
  void testSocialInsuranceDomainService_AgeLessThan40() {
    // 创建 Mock 的 PremiumBracket 实体对象
    PremiumBracket mockBracket = PremiumBracket.builder()
      .id(1L)
      .grade("21(18)")
      .stdRem(280000)
      .minAmount(270000)
      .maxAmount(290000)
      .healthNoCare(new BigDecimal("27776.00"))
      .healthCare(new BigDecimal("32228.00"))
      .pension(new BigDecimal("51240.00"))
      .build();

    Mockito.when(premiumBracketRepository.findByAmount(280000)).thenReturn(
      Mono.just(mockBracket)
    );

    StepVerifier.create(
      premiumBracketDomainService.socialInsuranceQuery(280000, 25)
    )
      .assertNext(result -> {
        assertThat(
          result.getEmployeeCost().getHealthCostWithNoCare()
        ).isEqualByComparingTo(new BigDecimal("13888.00"));
        assertThat(result.getEmployeeCost().getCareCost()).isEqualByComparingTo(
          new BigDecimal("0.00")
        ); // 40岁以下无介护保险
        assertThat(result.getEmployeeCost().getPension()).isEqualByComparingTo(
          new BigDecimal("25620.00")
        );

        assertThat(
          result.getEmployerCost().getHealthCostWithNoCare()
        ).isEqualByComparingTo(new BigDecimal("13888.00"));
        assertThat(result.getEmployerCost().getCareCost()).isEqualByComparingTo(
          new BigDecimal("0.00")
        );
        assertThat(result.getEmployerCost().getPension()).isEqualByComparingTo(
          new BigDecimal("25620.00")
        );
      })
      .verifyComplete();
  }

  @Test
  void testSocialInsuranceDomainService_NotFound() {
    Mockito.when(premiumBracketRepository.findByAmount(1050000)).thenReturn(
      Mono.empty()
    );

    StepVerifier.create(
      premiumBracketDomainService.socialInsuranceQuery(1050000, 30)
    )
      .expectErrorMatches(
        throwable ->
          throwable instanceof IllegalArgumentException &&
          throwable
            .getMessage()
            .contains("未找到月薪 1050000 对应的保险费等级")
      )
      .verify();
  }
}
