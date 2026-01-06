package jp.asatex.niuyuping.social_insurance_backend_service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jp.asatex.niuyuping.social_insurance_backend_service.entity.PremiumBracket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import org.springframework.test.context.ActiveProfiles;

@Testcontainers
@DataR2dbcTest
@ActiveProfiles("test")
public class SocialInsuranceRepositoryTest {

  @Container
  @ServiceConnection
  private static final PostgreSQLContainer<?> postgres =
    new PostgreSQLContainer<>("postgres:17.4");

  @Autowired
  private PremiumBracketRepository premiumBracketRepository;

  @Test
  void testFindBracketByAmount() {
    Mono<PremiumBracket> premiumBracket = premiumBracketRepository.findByAmount(
      550000
    );

    StepVerifier.create(premiumBracket)
      .assertNext(bracket -> {
        assertThat(bracket)
          .isNotNull()
          .usingRecursiveComparison()
          .ignoringFields("id", "createdAt", "updatedAt")
          .isEqualTo(PremiumBracket.builder()
            .grade("32(29)")
            .stdRem(560000)
            .minAmount(545000)
            .maxAmount(575000)
            .healthNoCare(new BigDecimal("55552.00"))
            .healthCare(new BigDecimal("64456.00"))
            .pension(new BigDecimal("102480.00"))
            .build());
      })
      .verifyComplete();
  }

  @Test
  void testFindBracketByAmount_IsNotFound() {
    Mono<PremiumBracket> premiumBracket = premiumBracketRepository.findByAmount(1050000);

    StepVerifier.create(premiumBracket)
      .expectNextCount(0)
      .verifyComplete();
  }
}
