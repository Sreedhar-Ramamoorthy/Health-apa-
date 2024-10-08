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
public class Quote extends Common {

  private String id;
  private String code;
  private Long entityId;
  private Integer productId;
  private String customerId;
  private String agentId;
  private Date startDate;
  private Date effectiveDate;
  private Date renewalDate;
  private Benefit benefit;
  private String status;
  private String paymentStyle;
  private Integer extPolicyId;
  private String extPolicyNumber;
  private Premium premium;
  private BigDecimal balance;
  private boolean isChildrenOnly;
  private boolean archived;
  private String quoteStatus;

}
