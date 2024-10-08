package ke.co.apollo.health.common.domain.model.remote;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import ke.co.apollo.health.common.enums.PaymentStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePolicyRequest {

  @NotBlank
  @Length(max = 30)
  private String productName;
  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private String policyStartDate;
  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private String policyEffectiveDate;
  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private String policyRenewalDate;
  @NotNull
  private Long policyHolderEntityId;
  @NotEmpty
  @Length(max = 20)
  private String policyStatus;
  @Builder.Default
  @Length(max = 30)
  private String policyCoverArea = "East Africa Region";
  @Builder.Default
  @Length(max = 30)
  private String policyResidentialTerritory = "Kenya";
  @Builder.Default
  @Length(max = 30)
  private String acquisitionChannel = "Other";
  @Builder.Default
  @Length(max = 30)
  private String levelOfCover = "Family";
  @Builder.Default
  @Length(max = 30)
  private String paymentStyle = PaymentStyle.MPESA.getValue();
  private String preferredCollectionDay;
  private String familyStatusRating;
  private String marketingValue;
  @NotNull
  private BigDecimal policyAmount;
  @Builder.Default
  private BigDecimal taxAmount = BigDecimal.ZERO;
  @Builder.Default
  private boolean suppressPriceCalculation = false;
  @Builder.Default
  private boolean generatePolicyNumber = true;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private String originalPolicyStartDate;
  @Builder.Default
  private boolean shortTermPolicy = false;
  @Builder.Default
  private boolean marketingFree = true;
  private String externalRef;
  @Builder.Default
  private Integer renewalCount = 0;
  @Builder.Default
  private Integer reimbursementClaimSubmissionPeriod = 60;
  @Builder.Default
  private Integer creditClaimSubmissionPeriod = 0;
  private String policyNumber;

}
