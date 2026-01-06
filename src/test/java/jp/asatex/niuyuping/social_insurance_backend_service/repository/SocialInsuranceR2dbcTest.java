package jp.asatex.niuyuping.social_insurance_backend_service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import jp.asatex.niuyuping.social_insurance_backend_service.entity.PremiumBracket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Testcontainers
@DataR2dbcTest
public class SocialInsuranceR2dbcTest {

  @Container
  @ServiceConnection
  private static final PostgreSQLContainer<?> postgres =
    new PostgreSQLContainer<>("postgres:17.4");

  @Autowired
  private R2dbcEntityTemplate r2dbcEntityTemplate;

  @Test
  void testFindByStdRem() {
    Flux<PremiumBracket> premiumBrackets = r2dbcEntityTemplate
      .select(PremiumBracket.class)
      .matching(
        Query.query(Criteria.where("std_rem").is(560000)).sort(
          Sort.by("std_rem").ascending()
        )
      )
      .all();

    StepVerifier.create(premiumBrackets)
      .assertNext(bracket -> {
        assertThat(bracket)
          .usingRecursiveComparison()
          .ignoringFields("id", "createdAt", "updatedAt")
          .isEqualTo(
            PremiumBracket.builder()
              .grade("32(29)")
              .stdRem(560000)
              .minAmount(545000)
              .maxAmount(575000)
              .healthNoCare(new BigDecimal("55552.00"))
              .healthCare(new BigDecimal("64456.00"))
              .pension(new BigDecimal("102480.00"))
              .build()
          );
      })
      .verifyComplete();
  }

  @Test
  void testFindByStdRem_IsNotFound() {
    Flux<PremiumBracket> premiumBrackets = r2dbcEntityTemplate
      .select(PremiumBracket.class)
      .matching(
        Query.query(Criteria.where("std_rem").is(1050000)).sort(
          Sort.by("std_rem").ascending()
        )
      )
      .all();

    StepVerifier.create(premiumBrackets).expectNextCount(0).verifyComplete();
  }
}
