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
public class ApplicationRenewalPolicy extends Common {

  private String quoteId;
  private String quoteNumber;
  private Long customerEntityId;
  private Integer productId;
  private String productName;
  private String customerId;
  private String intermediaryId;
  private Date startDate;
  private Date effectiveDate;
  private Date renewalDate;
  private String status;
  private String paymentStyle;
  private Integer policyId;
  private String policyNumber;
  private BigDecimal premiumAmount;
  private RenewalPremium premium;
  private BigDecimal balance;
  private String principalMember;
  private String mobileNumber;
  private boolean archived;

}
