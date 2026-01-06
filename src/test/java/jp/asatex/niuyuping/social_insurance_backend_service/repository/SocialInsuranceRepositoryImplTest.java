package jp.asatex.niuyuping.social_insurance_backend_service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jp.asatex.niuyuping.social_insurance_backend_service.entity.PremiumBracket;
import jp.asatex.niuyuping.social_insurance_backend_service.repository.impl.PremiumBracketRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation;
import org.springframework.data.relational.core.query.Query;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest(classes = PremiumBracketRepositoryImpl.class)
public class SocialInsuranceRepositoryImplTest {

  @Autowired
  private PremiumBracketRepositoryImpl premiumBracketRepositoryImpl;

  @MockitoBean
  private R2dbcEntityTemplate r2dbcEntityTemplate;

  @Test
  @SuppressWarnings("unchecked")
  void testFindBracketByAmount() {
    ReactiveSelectOperation.ReactiveSelect<PremiumBracket> reactiveSelect = 
        Mockito.mock(ReactiveSelectOperation.ReactiveSelect.class);
    ReactiveSelectOperation.TerminatingSelect<PremiumBracket> terminatingSelect = 
        Mockito.mock(ReactiveSelectOperation.TerminatingSelect.class);
    
    Mockito.when(r2dbcEntityTemplate.select(PremiumBracket.class))
        .thenReturn(reactiveSelect);
    Mockito.when(reactiveSelect.matching(Mockito.any(Query.class)))
        .thenReturn(terminatingSelect);
    Mockito.when(terminatingSelect.first())
        .thenReturn(Mono.just(
            PremiumBracket.builder()
                .id(1L)
                .grade("32(29)")
                .stdRem(560000)
                .minAmount(545000)
                .maxAmount(575000)
                .healthNoCare(new BigDecimal("55552.00"))
                .healthCare(new BigDecimal("64456.00"))
                .pension(new BigDecimal("102480.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        ));

    Mono<PremiumBracket> premiumBracket =
      premiumBracketRepositoryImpl.findBracketByAmount(550000);

    StepVerifier.create(premiumBracket)
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
  @SuppressWarnings("unchecked")
  void testFindBracketByAmount_IsNotFound() {
    ReactiveSelectOperation.ReactiveSelect<PremiumBracket> reactiveSelect = 
        Mockito.mock(ReactiveSelectOperation.ReactiveSelect.class);
    ReactiveSelectOperation.TerminatingSelect<PremiumBracket> terminatingSelect = 
        Mockito.mock(ReactiveSelectOperation.TerminatingSelect.class);
    
    Mockito.when(r2dbcEntityTemplate.select(PremiumBracket.class))
        .thenReturn(reactiveSelect);
    Mockito.when(reactiveSelect.matching(Mockito.any(Query.class)))
        .thenReturn(terminatingSelect);
    Mockito.when(terminatingSelect.first())
        .thenReturn(Mono.empty());

    Mono<PremiumBracket> premiumBracket = premiumBracketRepositoryImpl.findBracketByAmount(1050000);

    StepVerifier.create(premiumBracket)
      .expectNextCount(0)
      .verifyComplete();
  }
}
