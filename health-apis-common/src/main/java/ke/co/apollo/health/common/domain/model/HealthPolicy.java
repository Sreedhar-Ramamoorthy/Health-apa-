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
public class HealthPolicy extends Common {

  private Integer policyId;
  private String policyNumber;
  private String quoteId;
  private Long policyHolderId;
  private Integer productId;
  private Date startDate;
  private Date effectiveDate;
  private Date renewalDate;
  private Benefit benefit;
  private String status;
  private Premium premium;
  private BigDecimal balance;
  private RenewalPremium renewalPremium;
  private BigDecimal renewalBalance;
  private boolean archived;

}
