package ke.co.apollo.health.common.domain.model;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyDetail {

  private Integer policyId;
  private String policyNumber;
  private String policyHolderName;
  private String policyStatus;
  private Long policyHolderEntityId;
  private Integer productId;
  private String productName;
  private String principalMember;
  private Long principalId;
  private Date principalDob;
  private Date policyStartDate;
  private Date policyRenewalDate;
  private Date policyEffectiveDate;
  private String paymentStyle;
  private BigDecimal totalPremium;
  private BigDecimal premiumPaid;
  private BigDecimal premiumLeftToPay;


}
