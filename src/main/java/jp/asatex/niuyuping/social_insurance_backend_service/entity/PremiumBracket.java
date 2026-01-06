package jp.asatex.niuyuping.social_insurance_backend_service.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 保险费等级实体类
 * 对应 premium_bracket 表
 * 用于健康保险、厚生年金保险的计算
 */
@Table("premium_bracket")
public class PremiumBracket {

  @Id
  private Long id;

  @Column("grade")
  @NotNull
  @NotBlank(message = "等级不能为空")
  @Pattern(regexp = "^[0-9()]+$", message = "等级格式不正确")
  private String grade;

  @Column("std_rem")
  @NotNull(message = "标准报酬不能为空")
  @DecimalMin(value = "0.00", message = "标准报酬不能为负数", inclusive = true)
  @Digits(integer = 10, fraction = 2, message = "标准报酬格式不正确")
  private Integer stdRem;

  @Column("min_amount")
  @NotNull(message = "最小值不能为空")
  @DecimalMin(value = "0.00", message = "最小值不能为负数", inclusive = true)
  @Digits(integer = 10, fraction = 2, message = "最小值格式不正确")
  private Integer minAmount;

  @Column("max_amount")
  @NotNull(message = "最大值不能为空")
  @DecimalMin(value = "0.00", message = "最大值不能为负数", inclusive = true)
  @Digits(integer = 10, fraction = 2, message = "最大值格式不正确")
  private Integer maxAmount;

  @Column("health_no_care")
  @NotNull(message = "健康保险费（无护理）不能为空")
  @DecimalMin(value = "0.00", message = "健康保险费（无护理）不能为负数", inclusive = true)
  @Digits(integer = 10, fraction = 2, message = "健康保险费（无护理）格式不正确")
  private BigDecimal healthNoCare;

  @Column("health_care")
  @NotNull(message = "健康保险费（有护理）不能为空")
  @DecimalMin(value = "0.00", message = "健康保险费（有护理）不能为负数", inclusive = true)
  @Digits(integer = 10, fraction = 2, message = "健康保险费（有护理）格式不正确")
  private BigDecimal healthCare;

  @Column("pension")
  @NotNull(message = "厚生年金保险费不能为空")
  @DecimalMin(value = "0.00", message = "厚生年金保险费不能为负数", inclusive = true)
  @Digits(integer = 10, fraction = 2, message = "厚生年金保险费格式不正确")
  private BigDecimal pension;

  @Column("created_at")
  @CreatedDate
  private LocalDateTime createdAt;

  @Column("updated_at")
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Version
  private Long version;

  // 默认构造函数
  public PremiumBracket() {}

  // 全参构造函数
  public PremiumBracket(
    Long id,
    String grade,
    Integer stdRem,
    Integer minAmount,
    Integer maxAmount,
    BigDecimal healthNoCare,
    BigDecimal healthCare,
    BigDecimal pension,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long version
  ) {
    this.id = id;
    this.grade = grade;
    this.stdRem = stdRem;
    this.minAmount = minAmount;
    this.maxAmount = maxAmount;
    this.healthNoCare = healthNoCare;
    this.healthCare = healthCare;
    this.pension = pension;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.version = version;
  }

  // Getter 和 Setter 方法
  public Long getId() {
    return id;
  }

  public PremiumBracket setId(Long id) {
    this.id = id;
    return this;
  }

  public String getGrade() {
    return grade;
  }

  public PremiumBracket setGrade(String grade) {
    this.grade = grade;
    return this;
  }

  public Integer getStdRem() {
    return stdRem;
  }

  public PremiumBracket setStdRem(Integer stdRem) {
    this.stdRem = stdRem;
    return this;
  }

  public Integer getMinAmount() {
    return minAmount;
  }

  public PremiumBracket setMinAmount(Integer minAmount) {
    this.minAmount = minAmount;
    return this;
  }

  public Integer getMaxAmount() {
    return maxAmount;
  }

  public PremiumBracket setMaxAmount(Integer maxAmount) {
    this.maxAmount = maxAmount;
    return this;
  }

  public BigDecimal getHealthNoCare() {
    return healthNoCare;
  }

  public PremiumBracket setHealthNoCare(BigDecimal healthNoCare) {
    this.healthNoCare = healthNoCare;
    return this;
  }

  public BigDecimal getHealthCare() {
    return healthCare;
  }

  public PremiumBracket setHealthCare(BigDecimal healthCare) {
    this.healthCare = healthCare;
    return this;
  }

  public BigDecimal getPension() {
    return pension;
  }

  public PremiumBracket setPension(BigDecimal pension) {
    this.pension = pension;
    return this;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public PremiumBracket setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public PremiumBracket setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public Long getVersion() {
    return version;
  }

  public PremiumBracket setVersion(Long version) {
    this.version = version;
    return this;
  }

  // 流式编程风格的 with 方法，返回新实例
  public PremiumBracket withId(Long id) {
    return new PremiumBracket(
      id,
      this.grade,
      this.stdRem,
      this.minAmount,
      this.maxAmount,
      this.healthNoCare,
      this.healthCare,
      this.pension,
      this.createdAt,
      this.updatedAt,
      this.version
    );
  }

  public PremiumBracket withGrade(String grade) {
    return new PremiumBracket(
      this.id,
      grade,
      this.stdRem,
      this.minAmount,
      this.maxAmount,
      this.healthNoCare,
      this.healthCare,
      this.pension,
      this.createdAt,
      this.updatedAt,
      this.version
    );
  }

  public PremiumBracket withStdRem(Integer stdRem) {
    return new PremiumBracket(
      this.id,
      this.grade,
      stdRem,
      this.minAmount,
      this.maxAmount,
      this.healthNoCare,
      this.healthCare,
      this.pension,
      this.createdAt,
      this.updatedAt,
      this.version
    );
  }

  public PremiumBracket withMinAmount(Integer minAmount) {
    return new PremiumBracket(
      this.id,
      this.grade,
      this.stdRem,
      minAmount,
      this.maxAmount,
      this.healthNoCare,
      this.healthCare,
      this.pension,
      this.createdAt,
      this.updatedAt,
      this.version
    );
  }

  public PremiumBracket withMaxAmount(Integer maxAmount) {
    return new PremiumBracket(
      this.id,
      this.grade,
      this.stdRem,
      this.minAmount,
      maxAmount,
      this.healthNoCare,
      this.healthCare,
      this.pension,
      this.createdAt,
      this.updatedAt,
      this.version
    );
  }

  public PremiumBracket withHealthNoCare(BigDecimal healthNoCare) {
    return new PremiumBracket(
      this.id,
      this.grade,
      this.stdRem,
      this.minAmount,
      this.maxAmount,
      healthNoCare,
      this.healthCare,
      this.pension,
      this.createdAt,
      this.updatedAt,
      this.version
    );
  }

  public PremiumBracket withHealthCare(BigDecimal healthCare) {
    return new PremiumBracket(
      this.id,
      this.grade,
      this.stdRem,
      this.minAmount,
      this.maxAmount,
      this.healthNoCare,
      healthCare,
      this.pension,
      this.createdAt,
      this.updatedAt,
      this.version
    );
  }

  public PremiumBracket withPension(BigDecimal pension) {
    return new PremiumBracket(
      this.id,
      this.grade,
      this.stdRem,
      this.minAmount,
      this.maxAmount,
      this.healthNoCare,
      this.healthCare,
      pension,
      this.createdAt,
      this.updatedAt,
      this.version
    );
  }

  public PremiumBracket withCreatedAt(LocalDateTime createdAt) {
    return new PremiumBracket(
      this.id,
      this.grade,
      this.stdRem,
      this.minAmount,
      this.maxAmount,
      this.healthNoCare,
      this.healthCare,
      this.pension,
      createdAt,
      this.updatedAt,
      this.version
    );
  }

  public PremiumBracket withUpdatedAt(LocalDateTime updatedAt) {
    return new PremiumBracket(
      this.id,
      this.grade,
      this.stdRem,
      this.minAmount,
      this.maxAmount,
      this.healthNoCare,
      this.healthCare,
      this.pension,
      this.createdAt,
      updatedAt,
      this.version
    );
  }

  // 流式编程风格的 builder 方法
  public static PremiumBracketBuilder builder() {
    return new PremiumBracketBuilder();
  }

  public static class PremiumBracketBuilder {

    private Long id;
    private String grade;
    private Integer stdRem;
    private Integer minAmount;
    private Integer maxAmount;
    private BigDecimal healthNoCare;
    private BigDecimal healthCare;
    private BigDecimal pension;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    public PremiumBracketBuilder version(Long version) {
      this.version = version;
      return this;
    }

    public PremiumBracketBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public PremiumBracketBuilder grade(String grade) {
      this.grade = grade;
      return this;
    }

    public PremiumBracketBuilder stdRem(Integer stdRem) {
      this.stdRem = stdRem;
      return this;
    }

    public PremiumBracketBuilder minAmount(Integer minAmount) {
      this.minAmount = minAmount;
      return this;
    }

    public PremiumBracketBuilder maxAmount(Integer maxAmount) {
      this.maxAmount = maxAmount;
      return this;
    }

    public PremiumBracketBuilder healthNoCare(BigDecimal healthNoCare) {
      this.healthNoCare = healthNoCare;
      return this;
    }

    public PremiumBracketBuilder healthCare(BigDecimal healthCare) {
      this.healthCare = healthCare;
      return this;
    }

    public PremiumBracketBuilder pension(BigDecimal pension) {
      this.pension = pension;
      return this;
    }

    public PremiumBracketBuilder createdAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public PremiumBracketBuilder updatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public PremiumBracket build() {
      return new PremiumBracket(
        id,
        grade,
        stdRem,
        minAmount,
        maxAmount,
        healthNoCare,
        healthCare,
        pension,
        createdAt,
        updatedAt,
        version
      );
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PremiumBracket that = (PremiumBracket) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(grade, that.grade) &&
      Objects.equals(stdRem, that.stdRem) &&
      Objects.equals(minAmount, that.minAmount) &&
      Objects.equals(maxAmount, that.maxAmount) &&
      Objects.equals(healthNoCare, that.healthNoCare) &&
      Objects.equals(healthCare, that.healthCare) &&
      Objects.equals(pension, that.pension) &&
      Objects.equals(createdAt, that.createdAt) &&
      Objects.equals(updatedAt, that.updatedAt) &&
      Objects.equals(version, that.version)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      id,
      grade,
      stdRem,
      minAmount,
      maxAmount,
      healthNoCare,
      healthCare,
      pension,
      createdAt,
      updatedAt,
      version
    );
  }

  @Override
  public String toString() {
    return (
      "PremiumBracket{" +
      "id=" +
      id +
      ", grade='" +
      grade +
      '\'' +
      ", stdRem=" +
      stdRem +
      ", minAmount=" +
      minAmount +
      ", maxAmount=" +
      maxAmount +
      ", healthNoCare=" +
      healthNoCare +
      ", healthCare=" +
      healthCare +
      ", pension=" +
      pension +
      ", createdAt=" +
      createdAt +
      ", updatedAt=" +
      updatedAt +
      ", version=" +
      version +
      "}"
    );
  }
}
