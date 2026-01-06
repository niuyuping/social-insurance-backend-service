package jp.asatex.niuyuping.social_insurance_backend_service.entity;

import jakarta.validation.Validator;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeAll;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Assertions;

public class SocialInsuranceEntityTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testPremiumBracketValidation() {
        PremiumBracket premiumBracket = PremiumBracket.builder()
            .grade("32(29)")
            .stdRem(560000)
            .minAmount(545000)
            .maxAmount(575000)
            .healthNoCare(new BigDecimal("55552.00"))
            .healthCare(new BigDecimal("64456.00"))
            .pension(new BigDecimal("102480.00"))
            .build();
        Set<ConstraintViolation<PremiumBracket>> violations = validator.validate(premiumBracket);
        Assertions.assertTrue(violations.isEmpty(), "Validation should pass for valid PremiumBracket");
    }
    @Test
    public void testPremiumBracketValidationWithNegativeMinAmount() {
        PremiumBracket premiumBracket = PremiumBracket.builder()
            .grade("32(29)")
            .stdRem(560000)
            .minAmount(-1)
            .maxAmount(575000)
            .healthNoCare(new BigDecimal("55552.00"))
            .healthCare(new BigDecimal("64456.00"))
            .pension(new BigDecimal("102480.00"))
            .build();
        Set<ConstraintViolation<PremiumBracket>> violations = validator.validate(premiumBracket);
        Assertions.assertFalse(violations.isEmpty(), "Validation should fail for negative minAmount");
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("minAmount", violations.iterator().next().getPropertyPath().toString());
        Assertions.assertEquals("最小值不能为负数", violations.iterator().next().getMessage());
    }
}
