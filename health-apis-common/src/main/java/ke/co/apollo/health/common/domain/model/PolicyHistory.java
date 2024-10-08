package ke.co.apollo.health.common.domain.model;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyHistory extends Policy {

  private Integer policyId;
  private Integer productId;
  private Date policyStartDate;
  private Date policyEffectiveDate;
  private Date policyRenewalDate;
  private Long policyHolderEntityId;
  private String policyStatus;
  private String levelOfCover;
  private String paymentStyle;
  private BigDecimal policyAmount;
  private BigDecimal taxAmount;
  private Integer renewalCount;
  private String policyNumber;

}
